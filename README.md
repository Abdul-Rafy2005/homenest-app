# HomeNest

Spring Boot 3.5 + Thymeleaf app for managing vacation rental listings, bookings, reviews, and discounts across guest, host, and admin roles.

## Features
- Guest: browse/search listings (city/price filters), view details with ratings/reviews, check availability, and request bookings with conflict checks.
- Host: create/edit/publish listings with image upload, manage booking requests (confirm/reject), track earnings/stats, configure discounts per listing, and read guest reviews for owned properties.
- Admin: dashboards with KPIs, top listings, revenue/commission metrics; manage users (promote/demote/delete), listings (publish/delete), discounts, transactions, and export CSV sales data.
- Reviews: guests can post 1–5 star reviews only after a completed stay; average rating and counts shown on listing detail; hosts see consolidated feedback at `/host/reviews`.
- Discounts: host- and admin-managed percentage discounts with active/scheduled/expired status, assignable to multiple listings.

## Architecture
- Tech: Spring Boot 3.5, Spring MVC, Spring Data JPA (MySQL/H2), Spring Security, Thymeleaf UI.
- Modules: controllers per role (`Home`, `Listing`, `Booking`, `Host`, `Admin`, `Auth`), services for listings/bookings/users, JPA repositories for all entities (Listing, Booking, User, Review, Discount).
- Views: Thymeleaf templates under `src/main/resources/templates` (home, listing-detail, booking-create/success, guest/host/admin dashboards, reviews, discounts, transactions).

## Setup & Run (Local)
1) Prereqs: JDK 17, Maven, MySQL (or H2 for dev/testing).
2) Configure DB in `src/main/resources/application.properties` or env vars:
   - `spring.datasource.url=jdbc:mysql://localhost:3306/homenest_db`
   - `spring.datasource.username=<user>`
   - `spring.datasource.password=<pass>`
   - Adjust `spring.jpa.hibernate.ddl-auto` as needed (`update` by default).
3) Start: `mvn spring-boot:run` (from project root).
4) App entrypoints: `/` home/search, `/login`, `/register`, `/listings/{id}`, `/bookings/create` (with query params), `/host/**`, `/admin/**`.

## Core Flows
- Booking: guest selects dates → `/bookings/create` pre-validates date order and conflicts → submit POST `/bookings` → host can confirm/reject; admin commission computed (default 5%).
- Reviews: POST `/listings/{id}/reviews` allowed only if guest has a COMPLETED booking and hasn’t reviewed yet; rating 1–5 validation; messages via flash attributes.
- Host ops: CRUD listings with ownership checks; confirm/reject bookings; view earnings stats; manage discounts tied to listings; read all reviews for owned listings.
- Admin ops: dashboards and reports (revenue by day/month/city, top listings), CSV export, listing publish toggle/delete, user role changes, discount CRUD.

## Validations & Edge Cases
- Booking overlap: `BookingService.hasDateConflict` / `findOverlappingBookings` blocks date ranges that collide with existing bookings; GET form redirects with conflict message.
- Date sanity: end date must be after start date across booking flow; invalid ranges redirect with errors.
- Role/ownership checks: host/admin routes guarded via `@PreAuthorize` and explicit ownership checks before edit/delete/confirm/reject actions.
- Review gating: only one review per guest per listing; allowed only after COMPLETED booking; rating bounds enforced (1–5).
- Discount state: status derived from active flag and start/end dates (ACTIVE/SCHEDULED/EXPIRED) for display; listing assignment validated.
- File upload: listing image upload path `uploads/listings`; directory created on demand.
- Commission math: admin commission stored per booking (default 5% of totalAmount) and excluded from host earnings calculations.
- Data exposure: lazy relations may require view-layer care to avoid N+1 in templates if expanded.

## Security & Data Notes
- Passwords are currently stored in plain text (`User.password`); enable hashing (e.g., BCrypt) before production use.
- Update DB credentials and external API keys via environment variables; avoid committing secrets (current properties contain placeholders).
- Ensure proper CORS/CSRF and authentication configuration if exposing beyond trusted environment.

## Testing
- Base test scaffold in `HomenestApplicationTests`; extend with controller/service tests for booking overlaps, review eligibility, and role-guarded endpoints as you evolve the project.

## Possible Next Improvements
- Password hashing + login rate limiting.
- Dedicated availability calendar API for client-side date pickers.
- Email notifications for booking status changes.
- Pagination + filtering on admin/host tables.
- Cache/read models for dashboards to reduce DB load.
