# CareLink — New UI (drop-in frontend)

Complete visual redesign of the Healthcare Management System frontend.
**Backend untouched** — every `th:*` binding, form action, route, and field name is identical to your originals (verified by an automated binding check).

## What's inside

| Folder | What it is |
|---|---|
| `templates/` | 13 redesigned Thymeleaf pages (same filenames as your originals) |
| `templates/fragments/` | New `navbar.html` (public + authenticated, role-aware via `sec:authorize`), `alerts.html` (`all-alerts`, `success-alert`, `error-alert`), `footer.html` |
| `static/css/style.css` | Complete new design system — replaces your old stylesheet entirely |

## Design: "Calm Clinical Premium"
- Teal `#0F766E` on soft mint `#F4FAF8`, deep red emergency theme
- Sora display + Inter body (Google Fonts), Lucide-style inline SVG icons (zero emojis)
- Landing: mesh-gradient hero with staggered reveal, floating glass appointment card, specialty marquee, stat counters, emergency banner
- Dashboards: quick-action tiles, timeline appointment rows, queue cards, admin stat band
- Booking: 3-step progress indicator, doctor cards with avatar initials + fee, slot **chips**, sticky confirm bar
- Fully responsive (360px / 768px / 1280px), `prefers-reduced-motion` respected

## How to install
1. Back up your current `src/main/resources/templates/` and `src/main/resources/static/css/style.css`.
2. Copy `templates/` → `src/main/resources/templates/` (overwrites the 13 pages, adds `fragments/`).
3. Copy `static/css/style.css` → `src/main/resources/static/css/style.css` (replaces it).
4. Rebuild and run as usual (`./run-local.sh` or `mvn spring-boot:run`).

## Notes
- The product is now branded **CareLink** in titles/nav/footer (was "Healthcare Management System"). Change the word "CareLink" in the templates if you want your own name.
- The original `fragments/` and old `style.css` were not in the files you uploaded, so the fragments here are rebuilt from scratch with the **exact fragment names** your pages call (`navbar :: public`, `navbar :: authenticated`, `alerts :: all-alerts/success-alert/error-alert`, `footer :: footer`). If your real fragments had extra content, merge as needed.
- The navbar uses the same `sec:authorize` role blocks your README describes.
- The `108` helpline in the emergency pages/footer is India's emergency number — change if your deployment targets elsewhere.
