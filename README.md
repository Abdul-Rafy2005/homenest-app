# HomeNest App

HomeNest is a modern, Airbnb‑inspired accommodation platform built with Spring Boot and Thymeleaf. It focuses on Pakistan’s hostel market, providing realistic listings, clean booking flows, and a full-featured admin panel for business operations.

---

## 1. What HomeNest Offers

### For Guests
- Browse 40+ realistic hostel listings across 8 major Pakistani cities.
- Search by city and price range from a modern, responsive home page.
- View rich listing details with property type, capacity, address, and pricing.
- Create bookings with date validation and conflict checks.
- Track reservations through a “My Bookings” dashboard.
- Leave ratings and reviews after completed stays (one review per stay).

### For Hosts
- Manage your own listings from a dedicated Host Dashboard.
- Create, edit, publish/unpublish, and delete properties.
- See booking requests for owned listings, and confirm or reject them.
- Track basic earnings and booking stats across your properties.
- Read guest reviews consolidated for your listings.

### For Admins
- Operate a professional control panel with six main areas:
  - Dashboard
  - Listings
  - Users
  - Transactions
  - Sales Reports
  - Discounts
- Monitor KPIs: users, listings, bookings, revenue, and today’s performance.
- Analyze revenue by city and top-performing listings.
- Manage users (promote/demote, delete), listings (publish/delete), and discounts.
- Export transaction data as CSV for reporting and accounting.

---

## 2. Core Features

### Guest & Booking Experience
- Modern, mobile‑friendly browsing with an Airbnb‑style home page.
- Detailed listing pages with structured property info and a booking summary panel.
- Booking creation flow that validates dates and prevents overlapping reservations.
- Booking success page with clear confirmation and reference details.
- Guest bookings dashboard with status badges and clear history.

### Host Tools
- Host dashboard listing all properties owned by the host user.
- Clear table views for listings and booking requests.
- Simple flows to:
  - Create and manage property details.
  - Review, accept, or reject guest booking requests.

### Admin Control Panel

**Dashboard**
- High‑level KPIs for:
  - Total users (with Hosts/Guests/Admins breakdown).
  - Total listings (published vs unpublished).
  - Bookings and all‑time revenue.
- “Today’s Performance” metrics:
  - Today’s bookings.
  - Today’s revenue.
  - Active users for the day.
  - Current month revenue.
- Top 10 listings ranked by revenue, with city, bookings, and average rating.
- Recent transactions table with latest bookings.

**Listings Management**
- View all listings across all hosts.
- Filters by city, status (published/unpublished), and price range.
- Actions to view listing details, toggle publish state, and delete listings.
- Published/unpublished counts at a glance.

**User Management**
- Summary cards for total users and breakdown by role (Host, Guest, Admin).
- “New Users in last 30 days” metric.
- Role badge display with clear color‑coding.
- One‑click promote/demote between Guest and Host roles.
- Admin accounts protected from deletion.

**Transaction History**
- Date range filters with sensible defaults (last 30 days).
- Summary metrics: transaction count, total revenue, average booking value.
- Transactions table with guest, host, listing, nights, amount, and status.
- CSV export sharing the same filters, ready for spreadsheets and accounting.

**Sales Reports**
- Date‑driven reporting for revenue in a selected period.
- Revenue by city table sorted by revenue.
- Top listings in range with revenue and booking counts.
- Quick insights into which cities and listings perform best over time.

**Discount Management**
- Central interface for discount codes and promotions.
- Clearly labeled status badges (Active, Scheduled, Expired, Inactive).
- Create, edit, and delete discount codes with descriptions, percentages, and validity ranges.
- Status derived from active flag and date windows to avoid confusion.

---

## 3. Design & User Experience

HomeNest uses a consistent, modern design system:

- Airbnb‑inspired primary color palette centered around a vibrant red.
- Clean typography with system fonts and clear size hierarchy.
- Generous spacing, rounded cards, and soft shadows for elevation.
- Responsive layouts:
  - Multi‑column grids on desktop.
  - Two‑column listing details with a sticky booking panel.
  - Stacked, single‑column layouts on mobile.
- Clear status badges and pill labels for bookings, roles, and discounts.
- Reusable navbar and footer fragments for consistent navigation and branding.

All layouts are built to be mobile‑first and work smoothly on phones, tablets, and desktops.

---

## 4. Data & Seed Content

To make the app feel real out of the box, HomeNest includes seeded hostel data:

- Over 40 hostel listings across 8 major Pakistani cities, including:
  - Karachi
  - Lahore
  - Islamabad
  - Peshawar
  - Hyderabad
  - Quetta
  - Faisalabad
  - Multan
- Each listing:
  - Is owned by a sample host account.
  - Is published and visible on the home page by default.
  - Has realistic names, locations, capacities, and nightly prices in PKR.

This seed data is created automatically at startup if there are no existing listings in the database.

---

## 5. External API Integration (Optional)

HomeNest ships with an integration framework for importing external accommodation data:

- A dedicated service prepares for connecting to APIs such as Hostelworld or Booking.com.
- Configuration is controlled via properties and environment variables.
- Integration is disabled by default for safety; you must explicitly turn it on.
- The import flow:
  - Fetches listings from an external API.
  - Maps them to HomeNest’s internal listing model.
  - Assigns ownership to a configured host account.
- Admin endpoints and schedulers can trigger imports manually or on a schedule.

This framework is ready to be customized for your chosen API provider and mapping rules.

---

## 6. Security & Roles

HomeNest uses a role‑based security model and secure UI patterns:

- Roles:
  - **ROLE_GUEST** – regular users who browse and book.
  - **ROLE_HOST** – property owners managing listings and bookings.
  - **ROLE_ADMIN** – administrators with access to the full control panel.
- Admin endpoints live under the `/admin` path and are protected by role checks.
- Front‑end templates conditionally render navigation links based on the logged‑in user’s role.
- Admin features include:
  - Protected admin accounts (not deletable from the UI).
  - CSRF‑protected form submissions.
  - Confirmation dialogs before destructive operations.

Beyond platform roles, best‑practice recommendations include hashed passwords, secure storage of secrets, and HTTPS in production.

---

## 7. Architecture Overview

HomeNest is structured around clear separation of concerns:

- **Backend**
  - Spring Boot application with MVC pattern.
  - Controllers grouped by persona: Home, Listing, Booking, Host, Admin, Auth.
  - Service layer handling business logic for listings, bookings, users, discounts, and imports.
  - JPA repositories for data persistence (Listing, Booking, User, Review, Discount).

- **Frontend**
  - Thymeleaf templates for views: home, listing detail, booking flows, dashboards.
  - Shared fragments for navbar and footer.
  - Bootstrap 5 for layout and components, with custom CSS theming.

- **Reports & Analytics**
  - Repository‑level aggregation queries for revenue, city breakdowns, and top listings.
  - In‑memory grouping for complex admin reports.
  - CSV export for transactional data.

---

## 8. Getting Started

### Prerequisites
- JDK 17 or compatible Java version.
- Maven.
- MySQL or H2 database.
- A configured database schema and at least one administrator account.

### Basic Steps
1. Configure database connection and Hibernate settings.
2. Start the application using Maven or a built JAR.
3. Open the home page to browse seed listings.
4. Register or login as:
   - Guest: to test the booking flow.
   - Host: to manage host listings and booking requests.
   - Admin: to explore the full admin panel.

For a faster admin start, a default admin account can be used (if present in your environment).

---

## 9. Typical User Flows to Try

### Guests
- Browse the home page, filter by city and price, and open listing details.
- Create a booking for a selected property.
- View booking confirmations and status in “My Bookings”.
- Leave a review after a completed stay.

### Hosts
- Log in as a host user and open the Host Dashboard.
- Review existing seeded listings and create a new one.
- Inspect incoming booking requests and update their statuses.

### Admins
- Log in as an admin and open the Admin Dashboard.
- Explore:
  - KPI cards and today’s performance metrics.
  - Top listings and recent transactions.
  - Listings management with filters and publish toggles.
  - User management with role changes and protections.
  - Transaction history with date ranges and CSV export.
  - Sales reports by city and listing.
  - Discount management with status badges and CRUD actions.

---

## 10. Roadmap & Future Enhancements

Planned and recommended enhancements include:

- **User Experience**
  - Real hostel imagery via hosted assets or integrations like Unsplash.
  - More advanced search with date availability and combined filters.
  - Multi‑language support (e.g., Urdu and English).

- **Business Features**
  - Payment gateway integration for live payments.
  - Advanced host analytics and revenue insights.
  - Email or messaging notifications for booking status changes.

- **Technical Improvements**
  - Pagination for large tables in host and admin views.
  - Caching for frequently accessed dashboards and metrics.
  - Rate limiting and duplicate detection for external imports.
  - Additional test coverage for booking overlaps, security, and reporting.

---

## 11. Documentation

HomeNest includes extensive documentation for different audiences:

- **UI Modernization Summary**  
  Explains the design system, responsive layouts, and Pakistan hostel seed data.

- **UI Layout Reference**  
  Visual breakdowns of each major page, responsive behavior, and component styles.

- **External API Integration Guide**  
  Detailed instructions to configure and customize imports from third‑party listing providers.

- **Admin Area Documentation & Quick Start**  
  Deep technical reference and a practical walkthrough of the admin control panel.

- **Project Completion Summary**  
  High‑level overview of deliverables, build status, testing, and readiness for deployment.

These guides are intended to help developers, admins, and stakeholders understand the system, extend it safely, and operate it effectively.

---

HomeNest is designed as a production‑ready foundation for an accommodation platform centered on Pakistan’s hostel market, but it can be adapted to other regions or business models with minimal changes.
