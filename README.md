# Sri Lanka Travel Agency — Complete Full-Stack Project

Professional customer travel website and administration portal using React,
Spring Boot and MySQL.

## V3.1 exact-place image correction

- Destination cards no longer reuse one district photo for unrelated places.
- Automatic records search Wikimedia Commons using the destination name and district.
- Confirmed exact image URLs remain available immediately, with local fallbacks for failures.
- Admin custom image URLs have permanent priority through the database `image_mode` field.

## Project structure

```text
Sri_Lanka_Travel_Agency_Full_Stack/
├── frontend/       React + Vite customer website and admin portal
├── travel/         Spring Boot + JWT + JPA backend
└── database/       MySQL 8 schema and optional demo content
```

## Features

- Responsive green, blue and golden visual system
- Fully separate light and dark themes with persistent switch
- Five-scene cinematic hero slider, destination journeys and illustrated Sri Lanka road map
- Original high-resolution Sigiriya, tea-country, wildlife, coast and Galle visuals
- Complete 371-place catalogue across all 25 districts and eight travel themes
- District/category/featured search filters with destination detail and related-place views
- Register/login with JWT and CUSTOMER/ADMIN access
- Customer booking creation, tracking and cancellation
- Animated admin dashboard with destination district/category charts, booking statistics and revenue
- Advanced admin destination CRUD with image sources, tags, categories and homepage featuring
- Admin package, inquiry, booking and user management
- Public tailor-made trip form with admin inquiry/status workflow
- Admin booking/payment status and user-role management
- Search and price, duration, category and destination filters

## 1. MySQL

For a new database, open MySQL Workbench and run:

```text
database/travel_db_full.sql
```

The SQL is non-destructive: it creates or safely updates the schema and inserts
the supplied 371 destinations only when each name is missing. Existing records
and administrator edits are preserved. Run the entire script even when
`travel_db` already exists.

## 2. Backend

Set Windows User Environment Variables. Never write real secrets in Git:

```text
DB_PASSWORD=your MySQL password
JWT_SECRET=a long random Base64-encoded secret
ADMIN_NAME=Travel Admin
ADMIN_EMAIL=admin@travel.lk
ADMIN_PASSWORD=your strong admin password
```

For local development the included JWT fallback works, but set your own
`JWT_SECRET` before deployment. When `ADMIN_EMAIL` and `ADMIN_PASSWORD` are
present, startup creates the admin or safely synchronises an existing row to
the configured ADMIN role and BCrypt password.

Then open a new PowerShell:

```powershell
cd travel
mvn clean test
mvn spring-boot:run
```

Backend: `http://localhost:8080`

## 3. Frontend

Open a second PowerShell:

```powershell
cd frontend
Copy-Item .env.example .env
npm install
npm run dev
```

Frontend: `http://localhost:5173`

## Destination photographs

The customer website always has bundled local fallback visuals. Confirmed
major-place records contain curated Wikimedia Commons image redirects. Other
records resolve an exact-place Commons photo by destination name and district
instead of reusing one district image. Administrators can override any result
with a custom licensed image URL. See `frontend/IMAGE_CREDITS.md` for details.
