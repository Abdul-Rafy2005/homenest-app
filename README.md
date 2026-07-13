# HomeNest App

HomeNest is a modern accommodation platform built with Spring Boot 3.5, Thymeleaf, and PostgreSQL. It features a Boutique Editorial design system (Playfair Display + Work Sans, warm cream palette, 0px corners, 1px borders, no shadows) and runs fully containerized with Docker.

---

## 1. What HomeNest Offers

### For Guests
- Browse published listings across Pakistani cities.
- Search by city and price range from a responsive home page.
- View rich listing details with property type, capacity, address, and pricing.
- Create bookings with date validation and conflict checks.
- Track reservations through a "My Bookings" dashboard.
- Leave ratings and reviews after completed stays (one review per stay).

### For Hosts
- Manage your own listings from a dedicated Host Dashboard with dynamic KPIs.
- Create, edit, publish/unpublish, and delete properties.
- See booking requests for owned listings, and confirm or reject them.
- Track earnings, commission paid, and booking stats across your properties.
- Create and manage discount codes for your properties.

### For Admins
- Full control panel with six areas: Dashboard, Listings, Users, Transactions, Sales Reports, Discounts.
- Dynamic KPIs: users, listings, bookings, revenue, today's performance, month-to-date.
- Revenue by city and top-performing listings analysis.
- User management (promote/demote, delete), listing moderation (publish/delete), and discount CRUD.
- CSV export of transaction data.

---

## 2. Core Features

### Guest & Booking Experience
- Responsive home page with search by city and price range.
- Detailed listing pages with structured property info and booking panel.
- Booking creation flow with date validation and conflict prevention.
- Booking success page with confirmation and reference details.
- Guest bookings dashboard with status badges and history.

### Host Tools
- Host dashboard with dynamic KPIs (listings, bookings, earnings, commission).
- Quick actions for creating listings and navigating to bookings/reviews/discounts.
- Create and manage property details with image upload support.
- Review, accept, or reject guest booking requests.
- Discount management with percentage-based promotional codes.

### Admin Control Panel
- Dashboard with KPIs, top listings, recent transactions, and quick-access cards.
- Listings management with city/status/price filters.
- User management with role-based badges and protected admin accounts.
- Transaction history with date range filters and CSV export.
- Sales reports with city-wise revenue breakdown and top listings analysis.
- Discount management with status badges (Active, Scheduled, Expired, Inactive).

---

## 3. Design System

HomeNest uses a Boutique Editorial design system:

- **Typography**: Playfair Display (headlines/display) + Work Sans (body/labels)
- **Palette**: Warm cream surface (#fff8f2), deep brown text (#1e1b16), terracotta accent (#ab2e18)
- **Corners**: 0px border-radius everywhere (enforced globally)
- **Borders**: 1px solid rgba(30, 27, 22, 0.1) for cards, tables, and dividers
- **Shadows**: None (removed globally)
- **Components**: KPI row (auto-fit grid with 1px gap dividers), content cards, stat cards, badges, alerts, tables, buttons, inputs
- **Responsive**: Multi-column grids on desktop, 2-column on tablet, single-column on mobile

---

## 4. Tech Stack

- **Backend**: Spring Boot 3.5, Java 17, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5 (layout only), custom editorial CSS
- **Database**: PostgreSQL 16 (via Docker)
- **Containerization**: Docker Compose with multi-stage build

---

## 5. Seed Data

The application starts with a single admin user:

| Username | Email | Password | Role |
|----------|-------|----------|------|
| admin | admin@homenest.com | 1234 | ROLE_ADMIN |

All hostel listings have been removed. The homepage shows dynamic published listings from the database (empty state when none exist). Register new host and guest accounts through the application.

---

## 6. Getting Started

### Prerequisites
- JDK 17+
- Maven
- Docker & Docker Compose (recommended)

### Option A: Docker (Recommended)

```bash
# From the project root
docker compose up --build
```

This starts:
- **PostgreSQL 16** on port 5432
- **HomeNest app** on port 8080

The database is created automatically. Tables and seed data (admin user) are populated on first run.

### Option B: Local PostgreSQL

1. Install and start PostgreSQL 16.
2. Create a database:
   ```sql
   CREATE DATABASE homenest;
   ```
3. Configure connection in `src/main/resources/application.properties` or set environment variables:
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=homenest
   export DB_USER=your_user
   export DB_PASS=your_password
   ```
4. Build and run:
   ```bash
   mvn clean package -DskipTests
   java -jar target/homenest-app-0.0.1-SNAPSHOT.jar
   ```

### Access

Open `http://localhost:8080` in your browser.

---

## 7. Default Accounts

| Username | Email | Password | Role | Purpose |
|----------|-------|----------|------|---------|
| admin | admin@homenest.com | 1234 | ROLE_ADMIN | Full admin panel access |

Register new accounts through the `/register` page:
- **Guest** – browse, search, book properties, leave reviews.
- **Host** – create listings, manage bookings, create discounts.

---

## 8. User Flows to Try

### Guests
1. Register a guest account at `/register`.
2. Browse the home page and filter by city/price.
3. Open a listing and create a booking.
4. View booking status in "My Bookings".
5. Leave a review after a completed stay.

### Hosts
1. Register a host account at `/register`.
2. Open the Host Dashboard and create a new listing.
3. Publish the listing to make it visible to guests.
4. View and respond to booking requests (confirm/reject).
5. Create discount codes for your properties.

### Admins
1. Log in with admin/admin@homenest.com/1234.
2. Explore the Admin Dashboard with KPIs.
3. Manage users, listings, transactions, and discounts.
4. Run sales reports by city and date range.
5. Export transactions as CSV.

---

## 9. Architecture

```
src/main/java/com/homenest/homenest/
├── config/          # SecurityConfig
├── controller/      # Home, Listing, Booking, Host, Admin, Auth controllers
├── model/           # User, Listing, Booking, Review, Discount entities
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic layer

src/main/resources/
├── application.properties   # PostgreSQL config (DB_* env vars)
├── static/css/editorial.css # Boutique Editorial design system
├── static/images/           # Logo and assets
└── templates/               # Thymeleaf templates
    ├── fragments/           # navbar, footer
    ├── home.html            # Published listings with search
    ├── listing-detail.html  # Property details + booking panel
    ├── host-*.html          # Host dashboard, listings, bookings, reviews, discounts
    ├── guest-*.html         # Guest bookings
    ├── admin-*.html         # Admin dashboard, users, listings, transactions, reports, discounts
    └── *.html               # Auth, booking flow, about
```

---

## 10. Docker Configuration

### Dockerfile
Multi-stage build with BuildKit cache for fast rebuilds:
- **Stage 1**: Maven build with cached dependencies
- **Stage 2**: Lightweight JRE runtime

### docker-compose.yml
- `postgres`: PostgreSQL 16 Alpine, persistent volume, health check
- `app`: Spring Boot app, depends on healthy Postgres, auto-creates schema

### .dockerignore
Excludes `target/`, `.git/`, IDE files, logs, and Docker files from the build context.

---

## 11. Roadmap

- **Search**: Advanced filters with date availability
- **Payments**: Gateway integration for live transactions
- **Notifications**: Email/SMS for booking status changes
- **Analytics**: Advanced host analytics and revenue insights
- **Testing**: Comprehensive test coverage for booking overlaps and security
- **Performance**: Pagination, caching, rate limiting

---

## 12. License

This project is for educational and demonstration purposes.
