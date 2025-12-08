# 🚀 Admin Area Quick Start Guide

## Prerequisites
- HomeNest application running (Spring Boot on default port 8080)
- MySQL database with all tables created
- At least one admin user in database

## Access Admin Panel

### Step 1: Login as Admin
1. Navigate to: `http://localhost:8080/login`
2. Enter credentials:
   - **Username**: `admin`
   - **Password**: `password`
3. Click "Login"
4. You will be **automatically redirected** to `/admin/dashboard`

### Step 2: Explore Admin Features

#### 📊 Dashboard (Landing Page)
**URL**: `http://localhost:8080/admin/dashboard`

**What you'll see**:
- 6 KPI cards at top (Users, Listings, Bookings, Revenue, Today's metrics, Month revenue)
- Today's Performance section (3 compact metrics)
- Top 10 Performing Listings table (with ratings)
- Recent 10 Transactions table

**Quick actions**:
- Click "Manage →" links on KPI cards to jump to respective sections
- Click listing names to view listing details in new tab
- Click "View all transactions" to see full transaction history

---

#### 🏨 Listings Management
**URL**: `http://localhost:8080/admin/listings`

**Use cases**:
- View all properties across all hosts
- Filter by city, publish status, or price range
- Toggle publish/unpublish status
- Delete listings (with confirmation)

**How to filter**:
1. Enter city name (e.g., "Karachi")
2. Select status: All / Published / Unpublished
3. Enter price range (Min PKR - Max PKR)
4. Click "Filter" button
5. Click "Reset" to clear filters

**How to toggle publish**:
- Click eye-slash icon (👁️‍🗨️) next to listing
- Listing status updates immediately
- Success message appears at top

**How to delete**:
- Click trash icon (🗑️)
- Confirm in modal dialog
- Listing removed permanently

---

#### 👥 User Management
**URL**: `http://localhost:8080/admin/users`

**Use cases**:
- View all users (Guests, Hosts, Admins)
- See new user signups (last 30 days)
- Promote guests to hosts
- Demote hosts to guests
- Delete users (cannot delete admins)

**How to promote guest to host**:
1. Find guest user in table (gray badge)
2. Click "↑ Host" button
3. User role changes to HOST (blue badge)
4. Success message confirms

**How to demote host to guest**:
1. Find host user in table (blue badge)
2. Click "↓ Guest" button
3. User role changes to GUEST (gray badge)
4. Success message confirms

**How to delete user**:
1. Click trash icon (🗑️)
2. Confirm deletion in modal
3. User and all associated data removed
4. **Note**: Admin users are protected (cannot delete)

---

#### 💳 Transaction History
**URL**: `http://localhost:8080/admin/transactions`

**Use cases**:
- View all bookings in date range
- See summary metrics (total transactions, revenue, avg value)
- Export transactions to CSV

**How to filter by date**:
1. Select Start Date (defaults to 30 days ago)
2. Select End Date (defaults to today)
3. Click "Run Report"
4. Table updates with bookings in range

**How to export CSV**:
1. Set desired date range
2. Click "Export CSV" button (green)
3. File `transactions.csv` downloads
4. Open in Excel/Google Sheets

**CSV includes**:
- Booking ID, Date, Guest username, Host username
- Listing title, Nights count, Amount (PKR), Status

---

#### 📈 Sales Report
**URL**: `http://localhost:8080/admin/reports/sales`

**Use cases**:
- Analyze revenue by city
- Find top-performing listings in date range
- Track booking patterns over time

**How to run custom report**:
1. Select Start Date
2. Select End Date
3. Click "Run Report"
4. View two breakdown tables:
   - **Revenue by City**: City name, booking count, total revenue
   - **Top Listings**: Rank, listing name, city, revenue, booking count

**Insights to look for**:
- Which cities generate most revenue?
- Which listings are top performers in specific period?
- Average revenue per booking trends

---

#### 🏷️ Discount Management
**URL**: `http://localhost:8080/admin/discounts`

**Use cases**:
- Create promotional discount codes
- Set percentage discounts (e.g., 10% off)
- Define validity periods (start/end dates)
- Track discount status (Active/Expired/Scheduled/Inactive)

**How to create discount**:
1. Click "Create New Discount" button (top right)
2. Fill form:
   - **Code**: Unique promo code (e.g., "SUMMER2024")
   - **Description**: Internal note (e.g., "Summer sale 20% off")
   - **Percentage**: Discount amount (e.g., 20 for 20%)
   - **Start Date**: When discount becomes valid (optional)
   - **End Date**: When discount expires (optional)
   - **Active**: Check to enable discount
3. Click "Create Discount"
4. Success message confirms creation

**Status Badge Colors**:
- 🟢 **ACTIVE**: Discount is live and valid now
- 🔴 **INACTIVE**: Discount is disabled (active checkbox unchecked)
- 🔵 **SCHEDULED**: Start date is in future (not yet active)
- ⚫ **EXPIRED**: End date has passed (no longer valid)

**How to edit discount**:
1. Click pencil icon (✏️) next to discount
2. Update fields as needed
3. Click "Update Discount"
4. Changes saved

**How to delete discount**:
1. Click trash icon (🗑️)
2. Confirm in modal
3. Discount removed permanently

---

## 🧭 Navigation Tips

### Admin Dropdown Menu
Located in top navbar (right side), visible only when logged in as admin:
- 📊 Dashboard - Main overview
- 👥 Users - User management
- 🏨 Listings - Property management
- 💳 Transactions - Booking history
- 📈 Sales Report - Analytics
- 🏷️ Discounts - Promo codes

### Quick Navigation
- Click any menu item to jump to section
- Use "Back to Dashboard" button on each page
- Use browser back button to return to previous view
- Logout via user dropdown menu (top right)

---

## 🎯 Common Admin Tasks

### Daily Operations
1. **Check Today's Performance**: Dashboard → Today's Bookings/Revenue cards
2. **Review Recent Transactions**: Dashboard → Recent Transactions table
3. **Monitor New Signups**: Users → "New (Last 30d)" summary card
4. **Moderate Listings**: Listings → Filter by unpublished → Review → Toggle publish

### Weekly Reports
1. **Sales Analysis**: Sales Report → Set last 7 days → View revenue by city
2. **Top Performers**: Dashboard → Top Performing Listings table
3. **User Growth**: Users → Check new user count vs total users

### Monthly Tasks
1. **Revenue Review**: Dashboard → This Month Revenue card
2. **Expired Discounts**: Discounts → Check for gray "EXPIRED" badges → Delete old codes
3. **Seasonal Promotions**: Discounts → Create new codes for upcoming season
4. **Export Records**: Transactions → Set month range → Export CSV for accounting

---

## 🔧 Troubleshooting

### "Access Denied" on admin pages
- **Cause**: Not logged in or not ROLE_ADMIN
- **Fix**: Logout, login as `admin` user

### Admin menu not showing in navbar
- **Cause**: Logged in as Guest or Host (not Admin)
- **Fix**: Login with admin credentials

### Discount codes showing as "INACTIVE"
- **Cause**: Active checkbox was unchecked during creation
- **Fix**: Edit discount → Check "Active" checkbox → Update

### Top listings table empty
- **Cause**: No bookings in database yet
- **Fix**: Create test bookings, then refresh dashboard

### CSV export downloads empty file
- **Cause**: No bookings in selected date range
- **Fix**: Adjust date range to include known bookings

### Listings filter returns no results
- **Cause**: Filters too restrictive
- **Fix**: Click "Reset" button, try broader filters

---

## 🎓 Best Practices

### For Admins
1. **Regular Monitoring**: Check dashboard daily for anomalies
2. **User Verification**: Review new signups weekly, delete spam accounts
3. **Listing Quality**: Moderate unpublished listings promptly
4. **Discount Hygiene**: Delete expired discount codes monthly
5. **Export Backups**: Export transaction CSV monthly for records
6. **Security**: Never share admin credentials, logout when done

### For Reports
1. **Date Ranges**: Use consistent periods (7d, 30d, 90d) for trend analysis
2. **City Insights**: Compare city revenue to identify growth opportunities
3. **Listing Performance**: Promote top listings on homepage
4. **Booking Patterns**: Analyze peak seasons for dynamic pricing

---

## 🆘 Need Help?

**Admin Documentation**: See `ADMIN_AREA_DOCUMENTATION.md` for complete technical details

**Report Issues**: Contact development team with:
- Screenshot of issue
- URL where issue occurred
- Steps to reproduce
- Expected vs actual behavior

---

## ✅ Quick Test Checklist

After deployment, verify:
- [ ] Can login as admin and see dashboard
- [ ] All 6 KPI cards populate with data
- [ ] Can filter listings by city
- [ ] Can toggle listing publish status
- [ ] Can promote guest to host
- [ ] Can run transaction report with date range
- [ ] Can export CSV (downloads file)
- [ ] Can view sales report with city breakdown
- [ ] Can create new discount code
- [ ] All navbar links navigate correctly
- [ ] Logout works (redirects to login)

---

🎉 **Admin area ready for use!** Start with dashboard to get familiar with metrics, then explore each section.
