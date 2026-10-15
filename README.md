# Online Healthcare Management System

A comprehensive web-based healthcare management platform built with Java and Spring Boot. The system enables patients to book appointments with doctors, allows doctors to manage their schedules and patient records, and provides administrators with system-wide oversight.

## Recent Updates

### Fixed: Dynamic Seed Data (October 2026)
- **Issue:** Availability slots were hardcoded to past dates, causing "No doctors available" on booking and emergency lookup pages
- **Fix:** Updated `db/seed_data.sql` to use SQL `CURDATE()` and `DATE_ADD()` functions for dynamic slot generation
- **Result:** Slots now automatically adjust to the current week on every reseed, ensuring booking availability is always current
- **Verification:** See [Manual Verification Steps](#manual-verification-steps) in Running Tests section

### Fixed: Role-Based Navigation (October 2026)
- **Issue:** Navbar showed multiple role links simultaneously for logged-in users
- **Fix:** Updated navbar template to use `<th:block sec:authorize>` for proper Spring Security integration with Thymeleaf
- **Result:** Each user role (ADMIN, DOCTOR, PATIENT) now sees only their applicable navigation links

## Features

### Patient Features
- **Browse Doctors:** View available doctors by specialization and consultation fee
- **Book Appointments:** Select from available time slots
- **Appointment History:** Track past and upcoming appointments
- **Medical Records:** Access past medical records, diagnoses, and prescriptions
- **Emergency Services:** Find nearest hospitals by geographic location (no login required)

### Doctor Features
- **Appointment Management:** View, confirm, or reject patient appointments
- **Medical Records:** Add diagnosis, prescription, and clinical notes for completed appointments
- **Appointment Status Tracking:** Monitor appointment lifecycle (PENDING → CONFIRMED → COMPLETED)
- **Schedule Management:** Manage availability slots

### Administrator Features
- **User Management:** View all users, activate/deactivate accounts
- **System Oversight:** Monitor all appointments across the system
- **Access Control:** Manage role-based permissions

### Security Features
- **Role-Based Access Control:** Separate dashboards and permissions for patient, doctor, and admin roles
- **Appointment Authorization:** Doctors can only access their own appointments
- **Double-Booking Prevention:** Database unique constraint prevents concurrent bookings of the same slot
- **Password Security:** BCrypt hashing for all user passwords
- **Environment-Based Configuration:** Database credentials managed via environment variables, not committed to source code

## Technology Stack

- **Backend:** Java 17, Spring Boot 4.1.1
- **Framework:** Spring MVC, Spring Security, Spring Data JPA
- **Database:** MySQL 8.0+
- **Templating:** Thymeleaf
- **Frontend:** HTML5, CSS3
- **Build Tool:** Maven 3.9+
- **ORM:** Hibernate (via Spring Data JPA)

## Prerequisites

- **Java 17 or higher**
- **MySQL 8.0 or higher** (running and accessible)
- **Maven 3.9 or higher**
- **Git**
- **Bash shell** (Linux/macOS, or WSL on Windows)

## Quick Start — Local Development

### For the Project Developer (You)

If you're the original developer, use the automated local startup script:

#### 1. Store Your Database Credentials (First Time Only)

Create a private configuration file **outside the repository**:

```bash
mkdir -p ~/.config/healthcare-management-system
cat > ~/.config/healthcare-management-system/db.env << 'EOF'
DB_URL=jdbc:mysql://127.0.0.1:3306/healthcare_db
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
EOF
```

Restrict it to your user only:

```bash
chmod 600 ~/.config/healthcare-management-system/db.env
```

**Why this approach:**
- Credentials are stored **outside the repository** and **never committed to Git**
- The file is in your home directory (`~/.config`), not the project folder
- Only you can read it (permissions 600)
- Each developer/environment uses their own credentials
- No `.env` file in the project means no accidental credential leaks

#### 2. Start the Application

```bash
./run-local.sh
```

This script will:
- Load your credentials from `~/.config/healthcare-management-system/db.env`
- Validate the database connection
- Print a clear error if credentials are missing or invalid
- Start the application on `http://localhost:8080`

#### 3. First Time? Initialize the Database

Before first run, set up the database:

```bash
mysql -u your_mysql_username -p < db/schema.sql
mysql -u your_mysql_username -p healthcare_db < db/seed_data.sql
```

Then run `./run-local.sh`.

---

### For Others Cloning from GitHub

If you're cloning this project for the first time:

#### 1. Install Prerequisites
```bash
# Ubuntu/Debian
sudo apt-get install openjdk-17-jdk maven mysql-server mysql-client

# macOS (with Homebrew)
brew install openjdk@17 maven mysql
```

#### 2. Verify MySQL is Running
```bash
mysql -u root -p -e "SELECT 1;"
```

#### 3. Create Application Database
```bash
mysql -u root -p < db/schema.sql
mysql -u root -p healthcare_db < db/seed_data.sql
```

#### 4. Store Your Database Credentials
```bash
mkdir -p ~/.config/healthcare-management-system
cat > ~/.config/healthcare-management-system/db.env << 'EOF'
DB_URL=jdbc:mysql://127.0.0.1:3306/healthcare_db
DB_USERNAME=root
DB_PASSWORD=your_root_password
EOF

chmod 600 ~/.config/healthcare-management-system/db.env
```

#### 5. Run the Application
```bash
./run-local.sh
```

---

## Database Setup (Manual Method)

### 1. Create Database and Schema

```bash
mysql -u root -p < db/schema.sql
```

This creates:
- Database: `healthcare_db`
- Tables: users, doctors, patients, hospitals, doctor_availability, appointments, medical_records, feedback

### 2. Load Seed Data (Optional - for demo/testing)

The seed data script dynamically generates availability slots for the current week using `CURDATE()` and `DATE_ADD()`. This ensures the application always has fresh, relevant data:

```bash
mysql -u root -p healthcare_db < db/seed_data.sql
```

This populates the database with:
- 1 admin user (Anita Verma)
- 5 doctor users across 5 specializations
- 5 patient users
- 5 hospitals (located in Jaipur with realistic coordinates)
- **33 availability slots** (7 per doctor) covering today and the next 6 days
- Sample appointments and medical records

**Important:** The slot dates are generated relative to the current date (using `CURDATE()` in SQL), so booking availability is always current and does not require manual updates.

### 3. Verify Seed Data Loaded Correctly

```bash
mysql -u root -p healthcare_db << EOF
SELECT COUNT(*) as doctor_count FROM doctors;
SELECT COUNT(*) as slot_count FROM doctor_availability;
SELECT DISTINCT slot_date FROM doctor_availability ORDER BY slot_date;
EOF
```

Expected output:
- `doctor_count`: 5
- `slot_count`: 33
- `slot_date`: today through +6 days (e.g., 2026-10-08 through 2026-10-14)

## Environment Configuration

### Recommended Approach: External Credentials File (for local development)

See **[Quick Start — Local Development](#quick-start--local-development)** above. This is the safest method that prevents credential leaks.

### Alternative Approach: Using `.env` File in Project

If you prefer to use a `.env` file directly in the project:

1. Copy from template:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your credentials:
   ```
   DB_URL=jdbc:mysql://127.0.0.1:3306/healthcare_db
   DB_USERNAME=your_mysql_user
   DB_PASSWORD=your_mysql_password
   ```

3. Load and run:
   ```bash
   set -o allexport; source .env; set +o allexport
   ./mvnw spring-boot:run
   ```

**Important:** The `.env` file is in `.gitignore` and will never be committed. Each developer must create their own.

### How Spring Boot Loads Variables

The application reads environment variables in this order:

1. OS environment variables (set with `export DB_URL=...`)
2. Variables from `.env` file (if loaded with `source .env`)
3. Default values in `application.properties` (as fallback)

The `run-local.sh` script automates this process safely.

## Building the Application

### Compile and Package

```bash
./mvnw clean package -DskipTests
```

This generates `target/healthcare-management-system-0.0.1-SNAPSHOT.jar`.

### Run Locally

#### Recommended: Use the Startup Script

```bash
./run-local.sh
```

This script loads credentials from `~/.config/healthcare-management-system/db.env`, validates the database, and starts the application.

#### Manual: Load Environment and Run Maven

```bash
export DB_URL="jdbc:mysql://127.0.0.1:3306/healthcare_db"
export DB_USERNAME="your_mysql_username"
export DB_PASSWORD="your_mysql_password"
./mvnw spring-boot:run
```

#### Build and Run JAR

```bash
./mvnw clean package -DskipTests
DB_URL="..." DB_USERNAME="..." DB_PASSWORD="..." java -jar target/healthcare-management-system-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## Running Tests

### Execute Existing Tests

```bash
./mvnw test
```

This runs the integration test suite:
- `HealthcareManagementSystemApplicationTests.contextLoads()` — Verifies Spring context initializes, database connects, and seed data loads

### Manual Verification Steps

After starting the application at `http://localhost:8080`, verify the key features work:

#### 1. Patient Booking Page
- Log in with patient: `aman.gupta@mail.com` / `password123`
- Navigate to **Book Appointment** → /patient/book
- **Expected:** All 5 doctors display with specialization and consultation fee
- **If empty:** Check database has doctors and availability slots; see Troubleshooting below

#### 2. Emergency Services (No Login Required)
- Navigate to **Emergency Services** → /emergency
- Select area: "Malviya Nagar"
- **Expected:** Nearest hospitals display with available doctors for today
- **If no doctors shown:** Verify availability slots exist for today; see Troubleshooting below

#### 3. Slot Booking
- Log in as patient, select a doctor
- **Expected:** Available slots for today and future dates render
- **If no slots:** Database may need reseeding; see Troubleshooting below

## Default Credentials (After Seed Data)

All demo accounts use password: `password123` (BCrypt hashed)

### Admin Account
- **Email:** admin@ohms.com
- **Password:** password123

### Sample Doctor Accounts
- **Email:** rohan.mehta@ohms.com (Cardiology)
- **Email:** priya.rao@ohms.com (General Medicine)
- **Email:** arjun.singh@ohms.com (Orthopedics)
- **Email:** kavita.joshi@ohms.com (Pediatrics)
- **Email:** suresh.nair@ohms.com (Dermatology)

### Sample Patient Accounts
- **Email:** aman.gupta@mail.com
- **Email:** sneha.kapoor@mail.com
- **Email:** vikram.chauhan@mail.com
- **Email:** pooja.sharma@mail.com
- **Email:** rahul.yadav@mail.com

## Project Structure

```
src/
├── main/
│   ├── java/com/codehunters/healthcare/
│   │   ├── controller/           # Spring MVC controllers
│   │   ├── model/                # JPA entities
│   │   ├── repository/           # Data access (JPA repositories)
│   │   ├── service/              # Business logic
│   │   ├── security/             # Spring Security configuration
│   │   └── util/                 # Utility classes (e.g., LocationUtil)
│   └── resources/
│       ├── application.properties # Configuration (uses env vars)
│       ├── static/css/style.css   # Stylesheet
│       └── templates/             # Thymeleaf templates
└── test/
    └── java/...                  # Test classes
```

## Key Design Decisions

### Role-Based Access Control
- **Patient:** Can browse doctors, book appointments, view their own medical records
- **Doctor:** Can view appointments assigned to them, add medical records, confirm/reject appointments
- **Admin:** Can manage all users, view system-wide appointments

### Database Design
- **Normalized schema** with proper foreign keys and constraints
- **UNIQUE constraint** on `appointments.slot_id` prevents double-booking
- **Cascade delete** configured to maintain referential integrity
- **Seed data** uses BCrypt-hashed passwords for security

### Security Practices
- **Credential externalization:** Database passwords stored in environment variables, not in code
- **Authorization checks:** Doctors verified as appointment owners before access granted
- **Password security:** BCrypt encryption, no plaintext storage
- **Session management:** Spring Security handles HTTP sessions automatically
- **CSRF protection:** Spring Security CSRF tokens in forms (auto-configured)

## Workflows

### Patient Appointment Booking
1. Patient logs in
2. Browses available doctors
3. Selects a doctor to view available slots
4. Chooses a time slot and enters reason for visit
5. Confirms booking
6. Status: PENDING (awaiting doctor confirmation)

### Doctor Appointment Management
1. Doctor logs in and views appointments
2. Can confirm (status → CONFIRMED) or reject (status → CANCELLED)
3. Once confirmed, can add medical record (diagnosis, prescription, notes)
4. Upon record entry, status → COMPLETED

### Emergency Services
1. No login required
2. User selects their geographic area
3. System displays nearest hospitals and available doctors by distance
4. Uses Haversine formula for distance calculation

## Known Limitations

- **Patient profile updates:** Currently read-only. Patients cannot update their own information directly.
- **Feedback system:** Schema supports feedback but UI is not fully implemented.
- **Real GPS:** Emergency services use predefined geographic areas. In production, would integrate with device location or postal code API.
- **Test coverage:** Limited to integration test. Unit and controller tests for business logic are not included.

## Troubleshooting

### "Credentials file not found" Error from `run-local.sh`

**Cause:** You haven't created `~/.config/healthcare-management-system/db.env` yet.

**Solution:**
```bash
mkdir -p ~/.config/healthcare-management-system
cat > ~/.config/healthcare-management-system/db.env << 'EOF'
DB_URL=jdbc:mysql://127.0.0.1:3306/healthcare_db
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
EOF
chmod 600 ~/.config/healthcare-management-system/db.env
```

Then run `./run-local.sh` again.

### "Cannot connect to database" Error

**Cause:** Wrong credentials, MySQL not running, or database doesn't exist.

**Check:**
1. Is MySQL running?
   ```bash
   mysql -u root -p -e "SELECT 1;"
   ```

2. Does the database exist?
   ```bash
   mysql -u root -p -e "SHOW DATABASES LIKE 'healthcare_db';"
   ```

3. Are your credentials correct in `~/.config/healthcare-management-system/db.env`?

4. Create the database if needed:
   ```bash
   mysql -u root -p < db/schema.sql
   mysql -u root -p healthcare_db < db/seed_data.sql
   ```

### Database Connection Error When Starting Application

**Example error:**
```
Connection refused: Communications link failure
```

**Solutions:**
1. Verify MySQL is listening on port 3306:
   ```bash
   sudo netstat -tlnp | grep mysql
   ```

2. Check credentials are loaded by `run-local.sh` (it prints them before connecting)

3. Manually test the connection:
   ```bash
   mysql -h 127.0.0.1 -P 3306 -u your_username -p healthcare_db -e "SELECT 1;"
   ```

### "No doctors available" on Booking Page

**Cause:** Availability slots were seeded but the current date has moved past them.

**Solution:** Reseed the database:
```bash
mysql -u your_username -p healthcare_db < db/seed_data.sql
```

The `seed_data.sql` uses `CURDATE()` to generate slots for the current week, so reseeding fixes this.

### Port 8080 Already in Use

**Error:**
```
Caused by: java.net.BindException: Address already in use
```

**Solution:**
1. Kill the existing process:
   ```bash
   pkill -f "spring-boot:run"
   ```

2. Or use a different port by adding to your `.env`:
   ```bash
   SERVER_PORT=8081
   ```

### "No such file or directory" When Running `run-local.sh`

**Cause:** Script executed from wrong directory or path is incorrect.

**Solution:**
The script resolves its own directory, so you can run it from anywhere:
```bash
cd /path/to/healthcare-management-system
./run-local.sh

# Or from anywhere (if PATH includes the project):
/full/path/to/healthcare-management-system/run-local.sh
```

### Maven Wrapper Not Executable

**Error:**
```
permission denied: ./mvnw
```

**Solution:**
```bash
chmod +x ./mvnw
./mvnw spring-boot:run
```

### Tests Fail with Database Errors

**Cause:** Test database not initialized.

**Solution:**
1. Create and seed the test database:
   ```bash
   mysql -u root -p < db/schema.sql
   mysql -u root -p healthcare_db < db/seed_data.sql
   ```

2. Run tests with credentials:
   ```bash
   DB_URL="jdbc:mysql://127.0.0.1:3306/healthcare_db" \
   DB_USERNAME="your_username" \
   DB_PASSWORD="your_password" \
   ./mvnw test
   ```

---

## Security Notes

### Credential File Permissions

Always ensure `~/.config/healthcare-management-system/db.env` is readable only by you:

```bash
# Check permissions (should be 600)
ls -l ~/.config/healthcare-management-system/db.env

# Fix if needed
chmod 600 ~/.config/healthcare-management-system/db.env
```

### What's Committed to Git

✅ **Safe to Commit:**
- `.env.example` (template with placeholder values)
- `application.properties` (uses `${VAR:default}` syntax)
- `db/schema.sql` (database schema only)
- `db/seed_data.sql` (demo data with hashed passwords)
- `run-local.sh` (startup script)

❌ **Never Committed:**
- `.env` (actual local credentials)
- `~/.config/healthcare-management-system/db.env` (outside repo)
- Password hashes (only hashed demos in seed data)
- Real production credentials
- Logs, build artifacts, IDE files

### Git Ignore Check

```bash
git check-ignore .env
git check-ignore ~/.config/healthcare-management-system/db.env  # (outside repo, naturally ignored)
```

## Development Notes

### Adding a New Endpoint
1. Create a controller method with `@GetMapping` or `@PostMapping`
2. Use `@AuthenticationPrincipal UserDetails` to access logged-in user
3. Create a corresponding Thymeleaf template (if UI needed)
4. Spring Security will enforce role-based access via `@RequestMapping("/patient/**")`, etc.

### Adding a New Database Entity
1. Create a Java class annotated with `@Entity` and `@Table`
2. Use JPA annotations (`@Id`, `@Column`, `@ForeignKey`, etc.)
3. Create a `JpaRepository<YourEntity, ID>` interface
4. Update `db/schema.sql` if changing the database schema

## License

This project is developed for educational purposes as part of a university coursework.

## Support

For issues, questions, or contributions, please contact the development team or submit an issue through the project repository.
