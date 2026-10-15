# CareLink UI Fix + Premium Redesign — Implementation Plan

## Overview
CareLink is a Spring Boot 4.1.1 + Thymeleaf healthcare appointment management app with CSS design system already in place (`style.css`). Two critical bugs must be fixed in the navbar auth-state logic and appointment card date formatting, then the entire interface elevated to a premium institutional healthcare feel using a unified design system.

**Build command:** `mvn clean package -DskipTests`  
**Test/verification:** No automated tests exist; verification via `npm run dev` (if available) or `./run-local.sh` to start at `http://localhost:8080`.

---

## Critical Bug Fixes

### Bug 1: Navbar Auth-State Cluster (Critical)
**Current problem:** Navbar renders "Sign In" button + user avatar + "Logout" button ALL AT ONCE in overlapping cluster when authenticated, and shows multiple buttons even when signed out.

**File:** `src/main/resources/templates/fragments/navbar.html`

**Fix breakdown:**
- Fragment `authenticated`: Currently does NOT use conditional sec:authorize blocks around the entire auth-state section.
- Current structure renders all elements regardless of login state:
  - Avatar always shows (via `${#strings.substring(#authentication.name,0,1)}`)
  - Logout form always shows
  - Nav links show role-specific links but avatar+logout are always visible
- **Required change:** Wrap Sign In button (in `public` fragment) and the avatar+logout (in `authenticated` fragment) so that:
  - **Signed OUT** → show ONLY the public navbar with the single "Sign In" button
  - **Signed IN** → show ONLY the avatar with a dropdown menu (Profile, My Appointments, Logout)

**Avatar dropdown requirement:**
- DOM: Single `<span class="nav-avatar">` that acts as a trigger
- Clicking/hovering opens a dropdown menu (`.avatar-dropdown`) anchored underneath
- Menu items: "Profile", "My Appointments", "Logout"
- Close on: outside-click, Escape key, or clicking a menu item
- Keyboard navigation: Arrow keys, Tab, Enter to select

**Thymeleaf structure example:**
```html
<!-- Public navbar (for anonymous) -->
<nav th:fragment="public">
  ...
  <a th:href="@{/login}" class="btn nav-cta">Sign In</a>
</nav>

<!-- Authenticated navbar (for logged-in) -->
<nav th:fragment="authenticated">
  ...
  <!-- Role-aware nav links wrapped in sec:authorize -->
  <th:block sec:authorize="hasRole('PATIENT')">...</th:block>
  <th:block sec:authorize="hasRole('DOCTOR')">...</th:block>
  <th:block sec:authorize="hasRole('ADMIN')">...</th:block>
  
  <!-- Avatar dropdown (no more separate Logout button) -->
  <div class="avatar-wrapper">
    <span class="nav-avatar" id="avatar-trigger">A</span>
    <div class="avatar-dropdown" id="avatar-menu">
      <a href="/patient/profile">Profile</a>
      <a href="/patient">My Appointments</a>
      <form th:action="@{/logout}" method="post" class="dropdown-form">
        <button type="submit">Logout</button>
      </form>
    </div>
  </div>
</nav>
```

**CSS updates needed in `style.css`:**
- `.avatar-wrapper`: position relative container
- `.avatar-dropdown`: hidden by default, positioned absolutely below avatar with z-index, rounded, shadow, white background
- `.avatar-dropdown.open`: visibility/display toggle
- `.avatar-dropdown a, .avatar-dropdown button`: hover states, padding, block display
- Escape key and outside-click handled by **new JavaScript file** at `src/main/resources/static/js/navbar-dropdown.js`

**JavaScript file needed:** `src/main/resources/static/js/navbar-dropdown.js`
- On DOM ready, add event listeners to avatar trigger:
  - Click: toggle `.open` class on dropdown
  - Outside-click: close dropdown
  - Escape key: close dropdown
  - Arrow Down/Up: cycle through menu items
  - Enter on focused item: activate link or submit form

**Verification:**
- Start app at `http://localhost:8080`
- Visit `/` (home, anonymous): Should show "Sign In" button ONLY, no avatar
- Login as patient with `aman.gupta@mail.com` / `password123`
- Navigate to `/patient`: Should show avatar ONLY (no "Sign In", no separate "Logout" button)
- Click/hover avatar: Dropdown menu appears with Profile, My Appointments, Logout
- Click outside dropdown or press Escape: Dropdown closes
- Tab/arrow keys navigate menu items
- Check all role-based dashboards (/patient, /doctor, /admin): Avatar dropdown appears, no auth-state cluster

---

### Bug 2: Appointment Card Date Formatting (Critical)
**Current problem:** Appointment card (`.date-block` in patient-dashboard.html and other templates) displays raw date like "2026-10-10" wrapping mid-string, bare time like "10:00" with no AM/PM, and reason text truncated with no way to read full text.

**Files affected:**
- `src/main/resources/templates/patient-dashboard.html` (`.date-block` and `.appt-main`)
- `src/main/resources/templates/doctor-dashboard.html` (similar card structure)
- `src/main/resources/templates/patient-history.html` (medical records timeline)
- `src/main/resources/templates/emergency-results.html` (hospital doctor listings)
- `src/main/resources/templates/admin-dashboard.html` (appointment table)

**Current structure in patient-dashboard.html:**
```html
<div class="date-block">
  <b th:text="${appt.date}">--</b>          <!-- Raw date: "2026-10-10" -->
  <span th:text="${appt.time}">--</span>   <!-- Bare time: "10:00" -->
</div>
<div class="appt-main">
  <h4 th:text="${appt.doctorName}"></h4>
  <p class="sub">
    <span th:text="${appt.specialization}"></span> · 
    <span th:text="${appt.reason ?: 'General consultation'}"></span>  <!-- Truncated mid-word -->
  </p>
</div>
```

**Required changes:**

1. **Date formatting in Thymeleaf:**
   - Parse `appt.date` (assumed format: YYYY-MM-DD from Java `LocalDate`)
   - Use Thymeleaf's `#temporals` utility (built-in for java.time objects):
     ```html
     <b th:text="${#temporals.format(appt.date, 'dd MMM')}">10 Oct</b>
     <small th:text="${#temporals.format(appt.date, 'yyyy')}">2026</small>
     ```
   - Format time with AM/PM using `#temporals`:
     ```html
     <span th:text="${#temporals.format(appt.time, 'hh:mm a')}">10:00 AM</span>
     ```
   - If `appt.time` is a String (e.g., "10:00"), use Thymeleaf string utilities to append AM/PM logic (parse hour, inject AM/PM)

2. **CSS updates for `.date-block`:**
   ```css
   .date-block {
     white-space: nowrap;           /* Prevent any wrapping */
     overflow: hidden;               /* Hide overflow */
     text-overflow: ellipsis;        /* Not needed, but safe */
     display: flex;
     flex-direction: column;
     align-items: center;
     justify-content: center;
     width: 64px;                    /* Fixed width */
   }
   .date-block b {
     font-size: 22px;                /* Larger day+month */
     line-height: 1;
   }
   .date-block small {
     font-size: 11px;                /* Subtle year */
     color: var(--muted);
   }
   .date-block span {
     font-size: 13px;                /* Time with AM/PM */
     font-weight: 600;
     margin-top: 4px;
   }
   ```

3. **Reason text: 2-line clamp + hover reveal**
   - Add `title` attribute to reason span for full-text tooltip:
     ```html
     <span class="reason-text" 
           th:title="${appt.reason ?: 'General consultation'}"
           th:text="${appt.reason ?: 'General consultation'}">
     </span>
     ```
   - CSS for `.reason-text`:
     ```css
     .reason-text {
       display: -webkit-box;
       -webkit-line-clamp: 2;           /* Clamp to 2 lines */
       -webkit-box-orient: vertical;
       overflow: hidden;
       word-break: break-word;          /* Break mid-word only if necessary */
     }
     ```

4. **Card layout hardening for long strings:**
   - Wrap doctor name and reason in a container with `min-width: 0` to allow text truncation
   - Ensure `.appt-main` uses `flex: 1; min-width: 0` so it doesn't overflow parent

**Verification:**
- Start app at `http://localhost:8080`
- Login as patient: `aman.gupta@mail.com` / `password123`
- Navigate to `/patient` (dashboard)
- Look at appointment cards in "Your Appointments" section:
  - Date badge shows format like "10 Oct" (bold) / "2026" (small) with NO wrapping
  - Time shows "10:00 AM" (not bare "10:00")
  - Reason text displays gracefully on 2 lines max, no mid-word truncation
  - Hover over reason text: Full text appears in browser tooltip
- Verify long doctor names and specializations do NOT break card layout
- Check responsive view (360px mobile) — date badge remains readable, card doesn't overflow

---

## CSS Premium Design System Pass

**File:** `src/main/resources/templates/static/css/style.css`

Existing design system in place with `:root` CSS variables. **Task:** Audit existing tokens and enhance/add new ones to support institutional healthcare premium feel.

### Design Tokens Analysis

**Current token gaps identified:**
- Typography: Sora (display) + Inter (body/UI) already defined; type scale lacks intermediate sizes (13px, 14px, 14.5px, 15px, 15.5px used ad-hoc in CSS)
- Spacing: No explicit spacing scale; hardcoded 8px, 12px, 14px, 16px, 18px, 20px, 22px, 24px, 28px, 32px, 36px, 40px, 48px, 56px, 64px, 72px, 80px values throughout
- Shadows: 2 shadows defined (--shadow, --shadow-lg); no mid-range option
- Radii: 3 values (18px primary, 12px secondary, 999px pills); gap between 12px and 18px
- Colors: Good palette but missing tinted backgrounds for some semantic colors (info, warning, danger, success backgrounds already exist)

### Tokens to Add/Update in `:root`

**Add spacing scale (8px base, modular):**
```css
--space-xs:    4px;    /* Extra tight: icon gaps, small padding */
--space-sm:    8px;    /* Tight: form gaps, small margins */
--space-md:   12px;    /* Standard: component internal spacing */
--space-lg:   16px;    /* Medium: section gaps, larger padding */
--space-xl:   20px;    /* Large: card internal gaps */
--space-2xl:  24px;    /* Extra large: section padding */
--space-3xl:  32px;    /* Jumbo: major layout gaps */
--space-4xl:  40px;    /* Huge: between sections */
--space-5xl:  48px;    /* Massive: header padding, hero gaps */
--space-6xl:  56px;    /* Epic: footer gaps */
```

**Add type scale (rem-based for accessibility):**
```css
--text-xs:       12px;     /* 0.75rem - labels, captions */
--text-sm:       13px;     /* ~0.8rem - secondary text */
--text-base:     15px;     /* 0.9375rem - body text, form labels */
--text-lg:       16px;     /* 1rem - large body, section text */
--text-xl:       18px;     /* 1.125rem - card headers */
--text-2xl:      20px;     /* 1.25rem - smaller section headers */
--text-3xl:      24px;     /* 1.5rem - section headers */
--text-4xl:      30px;     /* 1.875rem - page headers */
--text-5xl:      36px;     /* 2.25rem - large page headers */
--text-6xl:      44px;     /* 2.75rem - hero headlines */
--text-7xl:      58px;     /* 3.625rem - hero display */
```

**Add shadow scale:**
```css
--shadow-sm:     0 2px 6px rgba(11,31,28,.06);        /* Subtle card, input focus */
--shadow-md:     0 6px 16px rgba(11,31,28,.10);       /* Standard card elevation */
--shadow:        0 10px 30px rgba(11,31,28,.08);      /* Current --shadow */
--shadow-lg:     0 20px 48px rgba(11,31,28,.14);      /* Current --shadow-lg */
--shadow-xl:     0 30px 60px rgba(11,31,28,.20);      /* High elevation: modals */
```

**Add radius scale:**
```css
--radius-xs:     6px;      /* Tiny: input focus ring, small pills */
--radius-sm:     12px;     /* Current --radius-s, badges, small cards */
--radius-md:     16px;     /* Buttons, form elements */
--radius:        18px;     /* Current --radius, main cards */
--radius-lg:     22px;     /* Large containers, modals */
--radius-xl:     26px;     /* Banner cards, hero sections */
--radius-full:   999px;    /* Buttons, pills (current --radius full) */
```

**Add focus ring:**
```css
--focus-ring:    0 0 0 4px rgba(15,118,110,.2);       /* Keyboard focus (teal, 20% alpha) */
--focus-ring-offset: 2px;                              /* Offset from element */
```

**Add transition easing & duration:**
```css
--duration-fast:  150ms;
--duration:       200ms;
--duration-slow:  300ms;
--ease-in-out:    cubic-bezier(.22,.8,.32,1);        /* Current --ease */
--ease-out:       cubic-bezier(.16,1,.3,1);
--ease-in:        cubic-bezier(.25,0,.46,.45);
```

### CSS Updates by Component

**1. Navigation (navbar)**
- Update `.nav`: Use `--space-md` for padding (12px instead of hardcoded 12px)
- Update `.nav-links a`: Use `--space-sm` for padding (8px 14px → 8px calc(var(--space-md) - 2px))
- Update `.nav-avatar`: size to 36px, ensure focus ring on keyboard navigation
- **NEW `.avatar-dropdown`:**
  ```css
  .avatar-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    background: var(--surface);
    border: 1px solid var(--line);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-lg);
    min-width: 180px;
    margin-top: var(--space-sm);
    opacity: 0;
    visibility: hidden;
    transform: translateY(-4px);
    transition: opacity var(--duration) var(--ease-in-out), 
                visibility var(--duration) var(--ease-in-out),
                transform var(--duration) var(--ease-in-out);
    z-index: 100;
  }
  .avatar-dropdown.open {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
  }
  .avatar-dropdown a,
  .avatar-dropdown button {
    display: block;
    width: 100%;
    text-align: left;
    padding: var(--space-md) var(--space-lg);
    border: none;
    background: none;
    color: var(--ink);
    font-size: var(--text-sm);
    font-weight: 500;
    cursor: pointer;
    transition: background var(--duration-fast);
  }
  .avatar-dropdown a:hover,
  .avatar-dropdown button:hover {
    background: var(--primary-xs);
  }
  .avatar-dropdown a:focus-visible,
  .avatar-dropdown button:focus-visible {
    outline: none;
    box-shadow: inset var(--focus-ring);
  }
  .avatar-dropdown a:first-child {
    border-radius: var(--radius-md) var(--radius-md) 0 0;
  }
  .avatar-dropdown a:last-child,
  .avatar-dropdown button:last-child {
    border-radius: 0 0 var(--radius-md) var(--radius-md);
  }
  ```

**2. Buttons**
- Update all `.btn` padding to use spacing vars: `padding: var(--space-sm) var(--space-lg)`
- Update `.btn-sm`: `padding: var(--space-xs) var(--space-md)`
- Update `.btn-lg`: `padding: var(--space-md) var(--space-xl)`
- Add focus-visible ring to all buttons:
  ```css
  .btn:focus-visible { outline: none; box-shadow: 0 0 0 4px rgba(15,118,110,.2); }
  ```
- Add consistent disabled state:
  ```css
  .btn:disabled, .btn[disabled] {
    opacity: 0.6;
    cursor: not-allowed;
    box-shadow: none;
  }
  ```

**3. Form inputs**
- Update all input padding to `var(--space-md)`
- Update border-radius to `var(--radius-md)`
- Add focus-visible ring:
  ```css
  input:focus-visible, select:focus-visible, textarea:focus-visible {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(15,118,110,.14);
  }
  ```

**4. Cards (`.card`, `.doctor-card`, etc.)**
- Update all padding to modular spacing: `padding: var(--space-2xl)` (24px) for standard cards
- Update border-radius to `var(--radius-md)` (16px) for tighter look
- Update box-shadow to `var(--shadow)`

**5. Appointment card (`.appt-row`)**
- Update date-block CSS per Bug 2 requirements
- Add `white-space: nowrap` and fixed sizing to prevent date wraps
- Apply spacing vars throughout

**6. Tables**
- Update thead padding: `var(--space-md)`
- Update tbody padding: `var(--space-lg)`
- Update hover transition to use duration var: `transition: background var(--duration-fast)`

**7. Badges & Pills**
- Update padding to `var(--space-xs) var(--space-md)` (5px 13px → 4px 12px for tighter look)
- Ensure minimum height for touch targets (44px for standalone, 32px for inline)

**8. Modals & Dropdowns**
- Add `.modal-overlay` with backdrop blur and semi-transparent background
- Add `.modal` with `var(--shadow-xl)` and `var(--radius-lg)`
- Ensure focus trap and Escape key close

**9. Forms & Validation**
- Add `.form-error` state: red border, red background tint
- Add `.form-success` state: green border, green background tint
- Add `.input-hint` for form help text: smaller font, muted color
- Add `.required` indicator (red asterisk)

**10. Empty states**
- Update `.empty-state` padding to `var(--space-5xl)` (56px)
- Ensure `.empty-icon` uses appropriate sizing and color

**11. Alerts**
- Update padding to `var(--space-md) var(--space-lg)`
- Update margin-bottom to `var(--space-xl)`
- Ensure consistent border and background

### Verification for CSS Pass

- Build: `mvn clean package -DskipTests`
- Open browser DevTools
- Check for CSS warnings (unlikely, but verify compiled CSS is clean)
- Visually inspect:
  - All buttons have consistent padding, radii, shadows
  - All form inputs have consistent styling and focus rings
  - All cards have consistent spacing, radii, shadows
  - Appointment dates display correctly without wraps
  - Badges and pills are properly sized
  - Navigation looks refined and cohesive
  - No hard-coded pixel values visible in rendered output (use modular spacing)

---

## Component Audit & Upgrade List

**Task:** Ensure every template file and every component class uses the premium design system consistently.

### Templates to Audit & Upgrade

1. **`index.html`** (Landing/hero)
   - Hero section: already styled well, ensure gradient use is refined (not overdone)
   - Marquee: already present
   - Stats band, services section, emergency banner: verify all use unified tokens
   - CTA band: ensure consistent button styling

2. **`login.html`**
   - Login form: ensure focus rings and error states are visible
   - Gradient side: ensure it's refined (not too flashy)

3. **`patient-dashboard.html`** (Dashboard)
   - Quick action tiles (`.qa-tile`): ensure consistent size, padding, hover state
   - Appointment cards (`.appt-row`): update per Bug 2 requirements
   - Empty state: ensure messaging and CTA are clear

4. **`book-doctor-list.html`** (Doctor list)
   - Doctor cards (`.doctor-card`): ensure titles, fees, buttons are well-aligned
   - Steps progress indicator: ensure visual clarity
   - Ensure long doctor names don't break layout

5. **`book-slots.html`** (Slot booking)
   - Slot selection buttons (`.slot-time`): ensure active/hover states are clear
   - Booking bar (`.booking-bar`): ensure sticky positioning and shadow are subtle but visible
   - Form validation messages: ensure clear error/success states

6. **`doctor-dashboard.html`** (Doctor appointments)
   - Appointment queue: ensure cards are well-spaced and readable
   - Action buttons: ensure consistent styling
   - Empty state: ensure message is clear

7. **`add-record.html`** (Medical record form)
   - Form fields: ensure labels, inputs, and validation messages are clear
   - Button states: ensure submit button is prominent but not alarming
   - Textarea for clinical notes: ensure adequate height and font

8. **`patient-history.html`** (Medical records timeline)
   - Record timeline (`.record-timeline`): ensure vertical line and items are clear
   - Record items: ensure dates, diagnosis, prescriptions are readable
   - Empty state: ensure message is appropriate

9. **`emergency.html`** & **`emergency-results.html`** (Emergency services)
   - Hero section: ensure urgent tone is calm, not alarming
   - Hospital grid: ensure cards display cleanly
   - Doctor badges: ensure they're easily readable
   - Distance pill: ensure it stands out

10. **`admin-dashboard.html`** (Admin panel)
    - Admin band header: ensure it's visually distinct but professional
    - Tables: ensure sticky headers, zebra rows (hover), responsive behavior
    - Action buttons in table rows: ensure they don't overflow on mobile
    - User role badges: ensure consistent color coding

11. **`fragments/navbar.html`** (Navigation)
    - Per Bug 1: Fix auth-state logic and add avatar dropdown
    - Ensure Emergency link is visually distinct (refined red, not alarming)
    - Ensure navbar is sticky and has subtle blur/border on scroll

12. **`fragments/alerts.html`** (Alert messages)
    - Ensure all alert types (danger, success, warning, info) use consistent styling
    - Ensure icons are present and sized consistently
    - Ensure close button (if present) is accessible

13. **`fragments/footer.html`** (Footer)
    - Verify dark background and light text have sufficient contrast (WCAG AA)
    - Ensure links are underlined or otherwise distinguishable
    - Ensure helpline CTA is visually distinct but not alarming

14. **`access-denied.html`** (Error page)
    - Error code display: ensure it's visually distinctive but professional
    - Error message: ensure it's clear and helpful
    - Back-to-home link: ensure it's prominent

### Component Classes to Update

| Component | Current Classes | Tokens to Use | Notes |
|-----------|-----------------|---------------|-------|
| Button primary | `.btn` | `--space`, `--radius-md`, `--shadow`, `--duration` | Already solid; verify focus ring added |
| Button secondary | `.btn-secondary` | Same as above | Already solid; verify focus ring added |
| Button danger | `.btn-danger` | `--danger`, `--duration` | Already solid; verify focus ring added |
| Form input | `input, select, textarea` | `--space-md` padding, `--radius-md`, `--focus-ring` | Add focus ring, ensure consistent borders |
| Form group | `.form-group` | `--space-*` for margins | Update hardcoded margins to vars |
| Card | `.card` | `--space-2xl` padding, `--radius-md`, `--shadow` | Update all hardcoded values |
| Badge | `.badge, .status-badge` | `--space-xs/md` padding, `--radius-full` | Ensure consistent sizing for touch |
| Navbar | `.nav, .nav-links` | `--space-*` for padding | Update hardcoded values |
| Avatar dropdown | `.avatar-dropdown` (NEW) | `--space-*`, `--radius-md`, `--shadow-lg` | Create per Bug 1 requirements |
| Appointment row | `.appt-row` | Per Bug 2 requirements | Fix date formatting, add 2-line clamp to reason |
| Date block | `.date-block` | `--space-*`, `white-space: nowrap` | Fix wrapping per Bug 2 |
| Timeline | `.record-timeline` | `--space-*`, `--radius-md` | Update spacing and radii |
| Table | `table, thead, tbody` | `--space-*` for padding/gaps | Update hardcoded padding |
| Empty state | `.empty-state` | `--space-5xl` padding, `--radius-md` | Ensure consistent sizing |
| Alert | `.alert` | `--space-md/lg` for padding/margins | Update hardcoded values |
| Hero section | `.hero` | Ensure gradients are refined, not excessive | Verify use of teal/amber only, no neon |
| Emergency banner | `.emg-banner` | `--emg` for danger red, `--space-5xl` | Ensure urgent tone is calm, professional |
| Modal (if added) | `.modal` | `--space-*`, `--radius-lg`, `--shadow-xl` | Add modal styling if needed by forms |

---

## Build & Verification Plan

**1. Compile and package:**
```bash
mvn clean package -DskipTests
```

**Expected result:** Successful build with no new warnings. Compiled `.jar` file in `target/`.

**2. Start application:**
```bash
./run-local.sh
```
Or manually:
```bash
export DB_URL="jdbc:mysql://127.0.0.1:3306/healthcare_db"
export DB_USERNAME="your_username"
export DB_PASSWORD="your_password"
./mvnw spring-boot:run
```

**Application should be available at:** `http://localhost:8080`

**3. Manual verification checklist:**

| Test | Route | Expected Outcome |
|------|-------|------------------|
| **Bug 1 — Auth state (anonymous)** | `/` (home) | Only "Sign In" button visible; no avatar, no logout button |
| **Bug 1 — Auth state (signed in)** | `/` (after login) | Only avatar visible; clicking/hovering opens dropdown with Profile, My Appointments, Logout options |
| **Bug 1 — Dropdown keyboard nav** | `/patient` or `/` (signed in) | Tab to avatar; Arrow Down opens dropdown; Arrow keys navigate items; Enter activates item; Escape closes |
| **Bug 1 — Dropdown outside-click** | `/patient` or `/` (signed in) | Avatar dropdown open; click outside menu → menu closes |
| **Bug 1 — All role navbars** | `/patient`, `/doctor` (login as each role), `/admin` | Avatar dropdown appears consistently; no auth-state cluster; role-specific nav links show correctly |
| **Bug 1 — Emergency nav distinct** | All authenticated pages | Emergency link is visually distinct (refined red treatment) and working |
| **Bug 2 — Date formatting** | `/patient` (patient dashboard) | Appointment dates show as "10 Oct" (bold) / "2026" (small) with NO wrapping, zero width issues |
| **Bug 2 — Time formatting** | `/patient` | Times show as "10:00 AM" (not bare "10:00") |
| **Bug 2 — Reason text 2-line clamp** | `/patient` | Long reason text wraps gracefully to max 2 lines; hover reveals full text in tooltip |
| **Bug 2 — Responsive card layout** | `/patient` at 360px | Cards don't overflow; date badge remains readable; appointment info is accessible |
| **Premium design — buttons** | Any page with buttons | All buttons have consistent padding, radii, shadows; focus rings visible on Tab navigation |
| **Premium design — forms** | `/login`, `/patient/book` | Form inputs have consistent styling; focus rings visible; labels clear; validation messages visible |
| **Premium design — cards** | `/patient`, `/patient/book`, `/doctor` | All cards have consistent spacing, radii, shadows; typography is readable |
| **Premium design — tables** | `/admin` | Table has sticky header; rows have hover state; responsive collapse on mobile |
| **Premium design — badges** | `/patient`, `/admin` | All status badges (Pending, Confirmed, Completed, Cancelled) have consistent colors, sizing, padding |
| **Premium design — empty states** | `/patient` (if no appointments) | Empty state is visually clear with icon, message, and CTA button |
| **Premium design — navigation sticky** | Any page (scroll down) | Navbar remains sticky at top; subtle blur and border on scroll are visible |
| **Premium design — alerts** | Login with wrong credentials, then correct | Alert messages display consistently with icon, color, and text |
| **Premium design — emergency page** | `/emergency` | Hero section is calm and clear (not alarming); hospital grid displays cleanly; distance and doctor badges are readable |
| **Premium design — responsive 360px** | All main pages at 360px width | Layout stacks correctly; buttons remain tappable (≥44px); text remains readable; no horizontal scroll except where necessary (tables) |
| **Contrast & accessibility** | All pages | Text has sufficient contrast (WCAG AA minimum); all interactive elements are keyboard-navigable; focus indicators are visible |
| **No console errors** | Browser DevTools Console | No JavaScript errors or warnings on main routes (/patient, /doctor, /admin, /emergency, /login, /) |

**4. Build verification:**
- No warnings in Maven output
- No CSS warnings in DevTools (Inspect > Styles tab)
- No unresolved images (broken icon SVGs, missing backgrounds)
- No layout shifts or FOUC (flash of unstyled content)

---

## Summary of Changes by Category

### Bug Fixes (2)
1. **Navbar auth-state logic fix** — Thymeleaf conditionals + avatar dropdown UI + JavaScript
2. **Appointment card date/time formatting** — Thymeleaf formatting + CSS hardening

### Design System Updates (CSS)
1. **Add modular spacing scale** — 8px-based rhythm
2. **Add type scale** — 12px to 58px with semantic names
3. **Add shadow scale** — 5 levels from subtle to high-elevation
4. **Add radius scale** — 5 levels from tight to full
5. **Add focus/transition tokens** — For keyboard navigation and smooth interactions

### Component Audits (14 templates, 20+ component classes)
1. Update all hardcoded spacing to spacing scale variables
2. Update all hardcoded font sizes to type scale variables
3. Update all hardcoded border-radii to radius scale variables
4. Add focus-visible rings to all interactive elements
5. Ensure consistent button, input, card, badge, table styling
6. Verify emergency page tone is calm and professional
7. Test responsive layouts at 360px and 1920px extremes

### Verification
- Build passes without warnings
- All routes load without console errors
- Manual UI testing passes 25-point checklist
- Responsive design works at mobile, tablet, desktop
- WCAG AA contrast compliance verified
- Keyboard navigation works on all interactive elements

---

## Constraints & Guardrails

- ✅ All existing routes and auth behavior must keep working
- ✅ No lorem ipsum; use realistic healthcare copy throughout
- ✅ Keep CareLink name and pulse-logo mark; refine, don't replace
- ✅ Responsive from 360px to desktop; test extremes
- ✅ Meet WCAG AA contrast for text
- ✅ Keep app fully keyboard-navigable
- ✅ This is a presentation-layer fix + redesign, not a backend rewrite
- ✅ No database schema changes
- ✅ No new dependencies (use existing Spring Boot, Thymeleaf, CSS)

---

## Implementation Order

**Phase 1: Bug Fixes (Critical)**
1. Fix navbar auth-state logic (Thymeleaf conditionals in navbar.html)
2. Create avatar-dropdown JavaScript (navbar-dropdown.js)
3. Create avatar-dropdown CSS (update style.css)
4. Fix appointment card date formatting (Thymeleaf #temporals, CSS updates)

**Phase 2: Design System Updates (CSS Tokens)**
5. Add all spacing, type, shadow, radius, transition tokens to :root in style.css
6. Update all component rules to use new tokens (button, form, card, badge, navbar, table, etc.)

**Phase 3: Component Audits (Template Updates)**
7. Audit and update each of 14 templates to ensure they use unified design system
8. Add focus-visible rings where missing
9. Test responsive layouts at 360px and desktop
10. Verify color contrast and keyboard navigation

**Phase 4: Build & Verify**
11. Build: `mvn clean package -DskipTests`
12. Start app: `./run-local.sh`
13. Manual verification against 25-point checklist
14. Fix any issues found during verification

