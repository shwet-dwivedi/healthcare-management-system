#!/bin/bash
#
# Healthcare Management System — Local Environment Setup
#
# This script helps you set up the application for local development.
# It will guide you through creating a .env file with your database credentials.

set -e

echo "Healthcare Management System — Environment Setup"
echo "=================================================="
echo ""

# Check if .env already exists
if [ -f .env ]; then
    echo "⚠️  .env file already exists. Skipping setup."
    echo "   To reconfigure, delete .env and run this script again."
    exit 0
fi

# Copy from template
if [ ! -f .env.example ]; then
    echo "❌ Error: .env.example not found."
    exit 1
fi

cp .env.example .env
echo "✅ Created .env from .env.example"

# Read current database credentials (if available)
echo ""
echo "Configure your local database connection:"
echo "  Default URL: jdbc:mysql://127.0.0.1:3306/healthcare_db"
echo "  Default User: krish"
echo ""
echo "Edit .env with your actual database credentials."
echo "Then run the application with:"
echo ""
echo "  export DB_URL=jdbc:mysql://127.0.0.1:3306/healthcare_db"
echo "  export DB_USERNAME=your_user"
echo "  export DB_PASSWORD=your_password"
echo "  ./mvnw spring-boot:run"
echo ""
echo "Or load from .env file:"
echo "  set -o allexport; source .env; set +o allexport"
echo "  ./mvnw spring-boot:run"
echo ""
