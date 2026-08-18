# Sri Lanka Travel Agency — Complete Full-Stack Project

Professional customer travel website and administration portal using React,
Spring Boot and MySQL.

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
- Original premium hero and luxury-resort imagery
- Animated home, destination, tour and hotel experiences
- Register/login with JWT and CUSTOMER/ADMIN access
- Customer booking creation, tracking and cancellation
- Admin dashboard statistics and revenue
- Admin CRUD for destinations, packages and 3–5 star hotels
- Admin booking/payment status and user-role management
- Hotel official website links and clear indicative-rate labels
- Search and price, duration, category, destination and star filters

## 1. MySQL

For a new database, open MySQL Workbench and run:

```text
database/travel_db_full.sql
```

For an existing `travel_db`, keep the existing data and start the updated
backend once with `spring.jpa.hibernate.ddl-auto=update`. Hibernate adds the
new `star_rating` and `website_url` hotel columns.

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
`JWT_SECRET` before deployment.

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

## Hotel rates and photographs

Room prices are dynamic. Demo values in the SQL file are explicitly
indicative and must be confirmed on each official hotel website. The frontend
provides an official-site button. Official hotel photographs are not copied;
the app uses its original local fallback image unless an administrator enters
an image URL they are permitted to use.

Official hotel references used for demo records:

- https://www.heritancehotels.com/kandalama/
- https://www.jetwinghotels.com/jetwinglighthouse/
- https://www.resort98acres.com/

See `frontend/IMAGE_CREDITS.md` for destination image attributions.
