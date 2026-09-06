# Sri Lanka Travel Agency - Complete Spring Boot Backend

This project matches the package used by the existing application:

```text
com.example.travel
```

Technology: Java 21, Spring Boot 3.3.4, Spring Security/JWT, Spring Data JPA,
MySQL and Maven.

V3 removes Hotels and Experiences from the customer navigation, admin menu and
dashboard. Their older backend classes/tables remain only for backward
compatibility with existing V2 databases; the V3 interface does not expose
those management sections.

## Included functions

- Customer registration and login with BCrypt password hashing
- JWT Bearer-token authentication
- `CUSTOMER` and `ADMIN` authorization
- Destination CRUD plus search, district, category and featured filters
- Automatic idempotent startup seed for 371 places across all 25 districts
- Tour package CRUD and search/filter
- Public tailor-made trip inquiries with admin planning statuses
- Customer booking creation, history and cancellation
- Admin booking/payment-status management
- Admin user and role management
- Admin dashboard statistics and paid revenue
- Validation, CORS and consistent JSON errors
- MySQL tables generated/updated through JPA

## Exact file locations

All paths below are relative to the backend project root (`travel/`):

```text
pom.xml
src/main/resources/application.properties
src/main/java/com/example/travel/TravelApplication.java

src/main/java/com/example/travel/config/AdminDataInitializer.java
src/main/java/com/example/travel/config/SecurityConfig.java

src/main/java/com/example/travel/controller/AdminUserController.java
src/main/java/com/example/travel/controller/AuthController.java
src/main/java/com/example/travel/controller/BookingController.java
src/main/java/com/example/travel/controller/DashboardController.java
src/main/java/com/example/travel/controller/DestinationController.java
src/main/java/com/example/travel/controller/HotelController.java
src/main/java/com/example/travel/controller/ExperienceController.java
src/main/java/com/example/travel/controller/TripInquiryController.java
src/main/java/com/example/travel/controller/TourPackageController.java

src/main/java/com/example/travel/dto/auth/AuthResponse.java
src/main/java/com/example/travel/dto/auth/LoginRequest.java
src/main/java/com/example/travel/dto/auth/RegisterRequest.java
src/main/java/com/example/travel/dto/booking/BookingRequest.java
src/main/java/com/example/travel/dto/booking/BookingResponse.java
src/main/java/com/example/travel/dto/booking/BookingStatusUpdateRequest.java
src/main/java/com/example/travel/dto/dashboard/DashboardResponse.java
src/main/java/com/example/travel/dto/packageinfo/TourPackageRequest.java
src/main/java/com/example/travel/dto/packageinfo/TourPackageResponse.java
src/main/java/com/example/travel/dto/user/RoleUpdateRequest.java
src/main/java/com/example/travel/dto/user/UserResponse.java

src/main/java/com/example/travel/entity/Booking.java
src/main/java/com/example/travel/entity/Destination.java
src/main/java/com/example/travel/entity/Hotel.java
src/main/java/com/example/travel/entity/Experience.java
src/main/java/com/example/travel/entity/TripInquiry.java
src/main/java/com/example/travel/entity/TourPackage.java
src/main/java/com/example/travel/entity/User.java

src/main/java/com/example/travel/enums/BookingStatus.java
src/main/java/com/example/travel/enums/PackageStatus.java
src/main/java/com/example/travel/enums/PaymentStatus.java
src/main/java/com/example/travel/enums/Role.java

src/main/java/com/example/travel/exception/BadRequestException.java
src/main/java/com/example/travel/exception/GlobalExceptionHandler.java
src/main/java/com/example/travel/exception/ResourceNotFoundException.java

src/main/java/com/example/travel/repository/BookingRepository.java
src/main/java/com/example/travel/repository/DestinationRepository.java
src/main/java/com/example/travel/repository/HotelRepository.java
src/main/java/com/example/travel/repository/TourPackageRepository.java
src/main/java/com/example/travel/repository/UserRepository.java

src/main/java/com/example/travel/security/CustomUserDetailsService.java
src/main/java/com/example/travel/security/JwtAuthenticationFilter.java
src/main/java/com/example/travel/security/JwtService.java

src/main/java/com/example/travel/service/AuthService.java
src/main/java/com/example/travel/service/BookingService.java
src/main/java/com/example/travel/service/DashboardService.java
src/main/java/com/example/travel/service/DestinationService.java
src/main/java/com/example/travel/service/HotelService.java
src/main/java/com/example/travel/service/TourPackageService.java
src/main/java/com/example/travel/service/UserService.java
```

## Safe installation into the existing project

1. Stop the current Spring Boot server with `Ctrl+C`.
2. Copy the existing `travel` folder as `travel-backup` before replacing files.
3. Copy this project's `pom.xml` and `src` folder into the existing `travel`
   project only after the backup is available.
4. Keep the existing working MySQL password configuration, or configure the
   `DB_PASSWORD` environment variable as described below.

## Database and environment configuration

The database must exist:

```sql
CREATE DATABASE IF NOT EXISTS travel_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

The default database username is `root`. Configure the password in Windows
User Environment Variables:

```text
Variable name: DB_PASSWORD
Variable value: your actual MySQL password
```

Close and reopen VS Code/PowerShell after setting a User Environment Variable.
Do not commit a real database password or JWT secret to GitHub.

Optional admin bootstrap variables:

```text
ADMIN_NAME=Travel Admin
ADMIN_EMAIL=admin@travel.lk
ADMIN_PASSWORD=use-a-strong-password
```

When these variables are configured, startup creates the admin or synchronises
an existing row with the same email to the ADMIN role and configured BCrypt
password. This repairs accounts previously inserted with a plain-text password.
When the variables are blank, automatic admin creation is skipped.

## Run

From the existing Windows project path:

```powershell
cd "C:\Users\induw\OneDrive\Desktop\Travelling_Booking\travel"
mvn clean test
mvn spring-boot:run
```

Backend base URL:

```text
http://localhost:8080
```

## Public endpoints

| Method | URL | Function |
|---|---|---|
| POST | `/api/auth/register` | Register customer |
| POST | `/api/auth/login` | Login and receive JWT |
| GET | `/api/destinations` | List/filter destinations |
| GET | `/api/destinations/{id}` | Destination details |
| GET | `/api/packages` | List/filter packages |
| GET | `/api/packages/{id}` | Package details |
| POST | `/api/inquiries` | Send a tailor-made trip request |

Search examples:

```text
GET /api/destinations?search=Ella
GET /api/destinations?district=Galle&category=Heritage%20%26%20History&featured=true
GET /api/packages?search=coast&category=Beach&destinationId=1&minPrice=100&maxPrice=1000&minDuration=2&maxDuration=10&status=ACTIVE
GET /api/hotels?search=Galle&available=true&starRating=5&maxPrice=100000
```

## Customer endpoints (Bearer token required)

| Method | URL | Function |
|---|---|---|
| POST | `/api/bookings` | Create own booking |
| GET | `/api/bookings/my` | View own bookings |
| PUT | `/api/bookings/{id}/cancel` | Cancel own booking |

## Admin endpoints (ADMIN Bearer token required)

| Method | URL | Function |
|---|---|---|
| POST/PUT/DELETE | `/api/destinations` | Manage destinations |
| POST/PUT/DELETE | `/api/packages` | Manage tour packages |
| GET | `/api/admin/inquiries` | View trip inquiries |
| PUT | `/api/admin/inquiries/{id}/status` | Update inquiry status |
| DELETE | `/api/admin/inquiries/{id}` | Delete inquiry |
| GET | `/api/admin/bookings` | View all bookings |
| PUT | `/api/admin/bookings/{id}/status` | Update booking/payment status |
| DELETE | `/api/admin/bookings/{id}` | Delete booking |
| GET | `/api/admin/users` | View users |
| PUT | `/api/admin/users/{id}/role` | Change role |
| DELETE | `/api/admin/users/{id}` | Delete user |
| GET | `/api/admin/dashboard` | Dashboard totals and revenue |

## Main request bodies

Register:

```json
{
  "name": "Induwara",
  "email": "induwara@example.com",
  "password": "StrongPass123",
  "phone": "0771234567"
}
```

Login:

```json
{
  "email": "induwara@example.com",
  "password": "StrongPass123"
}
```

Create destination (ADMIN):

```json
{
  "name": "Ella Rock",
  "location": "Badulla District, Sri Lanka",
  "district": "Badulla",
  "category": "Hiking & Adventure",
  "shortDescription": "A rewarding highland hike with wide valley views.",
  "description": "Full visitor-friendly destination description.",
  "imageUrl": "https://example.com/ella-rock.jpg",
  "imageSourceUrl": "https://example.com/photo-credit",
  "tags": "Hiking, Photography, Highlands",
  "featured": true
}
```

Create package (ADMIN):

```json
{
  "name": "Ella Adventure",
  "description": "Three-day hill-country tour",
  "price": 350.00,
  "durationDays": 3,
  "imageUrl": "https://example.com/package.jpg",
  "category": "Adventure",
  "status": "ACTIVE",
  "destinationId": 1
}
```

Create hotel (ADMIN):

```json
{
  "name": "Ella View Hotel",
  "location": "Ella",
  "description": "Mountain-view hotel",
  "pricePerNight": 75.00,
  "rating": 4.5,
  "starRating": 5,
  "imageUrl": "https://example.com/hotel.jpg",
  "websiteUrl": "https://hotel.example.com",
  "available": true
}
```

Create booking (CUSTOMER or ADMIN):

```json
{
  "packageId": 1,
  "travelDate": "2026-12-20",
  "numberOfGuests": 2
}
```

Admin booking-status update:

```json
{
  "bookingStatus": "CONFIRMED",
  "paymentStatus": "PAID"
}
```

For protected requests, add this Postman header using the token returned by
register/login:

```text
Authorization: Bearer YOUR_TOKEN
```
