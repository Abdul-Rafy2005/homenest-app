# HomeNest UI Layout Guide

## Modern Design System

### Color Palette
```
Primary (Airbnb Red):     #ff385c
Dark Text:                #222222
Light Text:               #717171
Border:                   #e5e7eb
Background Light:         #f8f9fa
Card Background:          #ffffff
```

### Typography
```
Font Family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif

Sizes:
  Hero Title:       3.5rem (desktop), 2rem (mobile)
  Page Title:       2rem
  Section Title:    1.3rem
  Card Title:       1rem
  Body Text:        0.95rem
  Small Text:       0.85rem, 0.9rem

Weights:
  Regular:   400
  Medium:    600
  Bold:      700
  Extra Bold: 800
```

### Spacing
```
Padding:     12px, 16px, 20px, 24px, 30px
Margin:      20px, 24px, 30px, 40px, 60px
Border Radius: 6px (buttons), 8px (inputs), 12px (cards)
Gap (Grid):    20px
```

## Page Layouts

### 1. Home Page (/)

```
┌─────────────────────────────────────────────────────────────┐
│  NavBar (White BG, Shadow)                                   │
│  🏠 HomeNest  |  Login  |  Register  | [User Dropdown]       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  HERO SECTION (Gradient)                                     │
│  Find Your Perfect Stay                                      │
│  Discover amazing places to stay in Pakistan and beyond      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  SEARCH CARD (White, Shadow, Rounded)                        │
│  [ City ] [ Min Price ] [ Max Price ] [ Search Btn ]         │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  LISTINGS GRID (Responsive 3-4 cards per row)               │
│                                                              │
│ ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│ │  [Image]     │  │  [Image]     │  │  [Image]     │        │
│ │  Title       │  │  Title       │  │  Title       │        │
│ │  Location    │  │  Location    │  │  Location    │        │
│ │  PKR 1500/n  │  │  PKR 1600/n  │  │  PKR 1700/n  │        │
│ │  [Details]   │  │  [Details]   │  │  [Details]   │        │
│ └──────────────┘  └──────────────┘  └──────────────┘        │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                       │
└─────────────────────────────────────────────────────────────┘
```

### 2. Listing Detail (/listings/{id})

```
┌─────────────────────────────────────────────────────────────┐
│  NavBar                                                       │
└─────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────┬─────────────────────┐
│                                       │                     │
│  [Image Gallery]                      │  BOOKING PANEL      │
│  (Gradient Placeholder)               │  (Sticky, Rounded)  │
│                                       │                     │
│  Listing Title                        │  PKR 1,500          │
│  📍 Lahore • Hostel • 12 guests      │  /night             │
│                                       │                     │
│  Property Details Grid                │  [Date Pickers]     │
│  ┌─────┬─────┬─────────┬─────┐      │  Check-in []        │
│ │Type │Capa │ Price   │Addr │       │  Check-out []       │
│ └─────┴─────┴─────────┴─────┘       │                     │
│                                       │  [Book Btn]         │
│  About This Place                     │  🛡️ Protected      │
│  (Full description paragraph...)      │                     │
│                                       │                     │
└──────────────────────────────────────┴─────────────────────┘

[← Back to Listings]

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                       │
└─────────────────────────────────────────────────────────────┘
```

### 3. Booking Confirmation (/bookings/create)

```
┌─────────────────────────────────────────────────────────────┐
│  NavBar                                                       │
└─────────────────────────────────────────────────────────────┘

                 ┌──────────────────────────┐
                 │  Confirm Your Booking    │
                 ├──────────────────────────┤
                 │                          │
                 │  Property Details        │
                 │  • Location: Lahore      │
                 │  • Type: Hostel          │
                 │  • Capacity: 12 guests   │
                 │                          │
                 │  Booking Details         │
                 │  • Check-in: Jan 15      │
                 │  • Check-out: Jan 18     │
                 │  • Nights: 3             │
                 │                          │
                 │  Price Breakdown         │
                 │  PKR 1,500 × 3 = 4,500   │
                 │  Total: PKR 4,500        │
                 │                          │
                 │  [Back] [Confirm Book]   │
                 └──────────────────────────┘

[← Back to Listing]

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                       │
└─────────────────────────────────────────────────────────────┘
```

### 4. Booking Success (/bookings/success)

```
┌─────────────────────────────────────────────────────────────┐
│  NavBar                                                       │
└─────────────────────────────────────────────────────────────┘

                 ┌──────────────────────────┐
                 │  ✓ Booking Confirmed!    │
                 ├──────────────────────────┤
                 │                          │
                 │        ✅ (large icon)    │
                 │                          │
                 │  Your booking created!   │
                 │  Ref: #456789            │
                 │                          │
                 │  Booking Summary         │
                 │  • Property: Mall Hostel │
                 │  • Check-in: Jan 15      │
                 │  • Check-out: Jan 18     │
                 │  • Total: PKR 4,500      │
                 │  • Status: ⏳ PENDING     │
                 │                          │
                 │  [Browse More] [View]    │
                 └──────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                       │
└─────────────────────────────────────────────────────────────┘
```

### 5. My Bookings (/bookings/guest/bookings)

```
┌─────────────────────────────────────────────────────────────┐
│  NavBar                                                       │
└─────────────────────────────────────────────────────────────┘

  🎫 My Bookings

┌─────────────────────────────────────────────────────────────┐
│ ID   Property        Check-in   Check-out  Nights  Total St │
├─────────────────────────────────────────────────────────────┤
│ #1   Mall Hostel     Jan 15     Jan 18     3      4500  ⏳   │
│ #2   Sea View Stay   Feb 1      Feb 5      4      7200  ✓   │
│ #3   Clifton Rest    Feb 10     Feb 12     2      4400  ✓   │
└─────────────────────────────────────────────────────────────┘

[Browse Listings]

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                       │
└─────────────────────────────────────────────────────────────┘
```

### 6. Host Dashboard (/listings/host/listings)

```
┌─────────────────────────────────────────────────────────────┐
│  NavBar                                                       │
└─────────────────────────────────────────────────────────────┘

  My Listings          [View Booking Requests] [+ Create New]

┌─────────────────────────────────────────────────────────────┐
│ ID  Title           City      Type    Price  Capacity Pub   │
├─────────────────────────────────────────────────────────────┤
│ 1   Mall Hostel     Lahore    HOSTEL  1600   16      ✓      │
│ 2   Sea View Stay   Karachi   HOSTEL  1800   12      ✓      │
│ 3   Clifton Rest    Karachi   HOSTEL  2200   4       ✓      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Footer                                                       │
└─────────────────────────────────────────────────────────────┘
```

## Responsive Behavior

### Desktop (≥1200px)
- Hero: 3.5rem title
- Grid: 3-4 cards per row
- Listing Detail: 2 columns (8/12 + 4/12)
- Tables: Full width, horizontal scroll

### Tablet (768px - 1199px)
- Hero: 2.5rem title
- Grid: 2 cards per row
- Listing Detail: Still 2 columns, adjusted widths
- Tables: Scrollable on small screens

### Mobile (<768px)
- Hero: 2rem title, smaller padding
- Grid: 1 card per row (full width)
- Listing Detail: 1 column, stacked layout
- Booking Panel: Below content, not sticky
- Tables: Horizontal scroll or cards
- Navbar: Hamburger menu

## Interactive Elements

### Hover Effects
```css
.listing-card:hover {
  box-shadow: 0 10px 25px rgba(0,0,0,0.15);
  transform: translateY(-5px);  /* Lift effect */
}

.btn-primary:hover {
  background: #e71f40;  /* Darker red */
}

table tbody tr:hover {
  background: #f8f9fa;  /* Highlight */
}
```

### Focus States (Accessibility)
```css
input:focus {
  border-color: #ff385c;
  box-shadow: 0 0 0 0.2rem rgba(255, 56, 92, 0.15);
}
```

## Component Styles

### Cards
```
Border Radius: 12px
Box Shadow: 0 1px 3px rgba(0,0,0,0.1)
Padding: 16px (small), 24px (large)
Background: #ffffff
```

### Buttons
```
Primary (#ff385c):
  Padding: 12px 24px
  Border Radius: 8px
  Font Weight: 600
  Color: white
  Hover: #e71f40

Secondary (outline):
  Border: 1px solid #ff385c
  Color: #ff385c
  Background: transparent
  Hover: Light background
```

### Badges/Status
```
PENDING:    Background: #ffc107, Color: #000
CONFIRMED: Background: #28a745, Color: white
CANCELLED: Background: #dc3545, Color: white
COMPLETED: Background: #6c757d, Color: white
```

## Accessibility

- ✅ Semantic HTML (header, nav, main, footer)
- ✅ ARIA labels on dropdowns and modals
- ✅ Sufficient color contrast (WCAG AA)
- ✅ Focus indicators on interactive elements
- ✅ Keyboard navigation support
- ✅ Alt text placeholders for images

---

**Note:** All layouts use Bootstrap 5 Grid System and are fully responsive.
