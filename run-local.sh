#!/bin/bash
#
# Healthcare Management System — Local Startup Script
#
# This script safely loads database credentials from a private configuration file,
# validates them, and starts the Spring Boot application.
#
# Configuration file location: ~/.config/healthcare-management-system/db.env
# (File is NOT committed to Git and is only on your local machine)
#

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Resolve the project directory (works when run from any directory)
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Configuration file path
CREDENTIALS_FILE="$HOME/.config/healthcare-management-system/db.env"

# Function to print colored messages
print_error() {
    echo -e "${RED}❌ Error: $1${NC}" >&2
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "ℹ️  $1"
}

# Check if credentials file exists
if [ ! -f "$CREDENTIALS_FILE" ]; then
    print_error "Credentials file not found: $CREDENTIALS_FILE"
    echo ""
    echo "Setup instructions:"
    echo "=================="
    echo "1. Create the configuration directory:"
    echo "   mkdir -p ~/.config/healthcare-management-system"
    echo ""
    echo "2. Create $CREDENTIALS_FILE with your database credentials:"
    echo "   cat > ~/.config/healthcare-management-system/db.env << 'EOF'"
    echo "DB_URL=jdbc:mysql://127.0.0.1:3306/healthcare_db"
    echo "DB_USERNAME=your_mysql_username"
    echo "DB_PASSWORD=your_mysql_password"
    echo "EOF"
    echo ""
    echo "3. Secure the file (readable only by you):"
    echo "   chmod 600 ~/.config/healthcare-management-system/db.env"
    echo ""
    echo "4. Then run this script again:"
    echo "   $0"
    echo ""
    exit 1
fi

# Check file permissions (warn if world-readable)
PERMS=$(stat -c %a "$CREDENTIALS_FILE" 2>/dev/null || stat -f %A "$CREDENTIALS_FILE" 2>/dev/null)
if [[ "$PERMS" != "600" && "$PERMS" != "rw-------" ]]; then
    print_warning "Credentials file has permissive permissions: $PERMS"
    print_info "Consider restricting to: chmod 600 $CREDENTIALS_FILE"
fi

# Load credentials from file
# Using a subshell to prevent variables from polluting the parent environment
export $(grep -v '^#' "$CREDENTIALS_FILE" | xargs)

# Validate required variables
if [ -z "$DB_URL" ]; then
    print_error "DB_URL not set in $CREDENTIALS_FILE"
    exit 1
fi

if [ -z "$DB_USERNAME" ]; then
    print_error "DB_USERNAME not set in $CREDENTIALS_FILE"
    exit 1
fi

# DB_PASSWORD can be empty (for local dev), so we don't require it

# Validate database connectivity (optional but helpful)
print_info "Validating database configuration..."

# Extract host and database name from JDBC URL
# Format: jdbc:mysql://host:port/database
if [[ $DB_URL =~ jdbc:mysql://([^:]+):?([0-9]*)/(.+) ]]; then
    DB_HOST="${BASH_REMATCH[1]}"
    DB_PORT="${BASH_REMATCH[2]:-3306}"
    DB_NAME="${BASH_REMATCH[3]}"
else
    print_error "Invalid DB_URL format: $DB_URL"
    echo "Expected format: jdbc:mysql://host:port/database"
    exit 1
fi

# Test MySQL connection
if ! mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USERNAME" -p"$DB_PASSWORD" -e "SELECT 1;" "$DB_NAME" >/dev/null 2>&1; then
    print_error "Cannot connect to database"
    echo "Details:"
    echo "  Host: $DB_HOST"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo "  Username: $DB_USERNAME"
    echo ""
    echo "Troubleshooting:"
    echo "  1. Verify MySQL is running: mysql -u $DB_USERNAME -p -e 'SELECT 1;'"
    echo "  2. Check credentials in: $CREDENTIALS_FILE"
    echo "  3. Ensure database exists: mysql -u $DB_USERNAME -p -e 'SHOW DATABASES;'"
    echo ""
    exit 1
fi

print_success "Database connection verified"
echo ""

# Print startup information
print_info "Starting Healthcare Management System..."
print_info "Database: $DB_NAME on $DB_HOST:$DB_PORT"
print_info "Username: $DB_USERNAME"
echo ""
print_info "Application will start on http://localhost:8080"
echo ""

# Change to project directory and start Maven
cd "$PROJECT_DIR"
./mvnw spring-boot:run
