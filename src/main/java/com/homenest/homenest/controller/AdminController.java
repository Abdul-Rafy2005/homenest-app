package com.homenest.homenest.controller;

import com.homenest.homenest.model.Booking;
import com.homenest.homenest.model.Discount;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.BookingRepository;
import com.homenest.homenest.repository.ListingRepository;
import com.homenest.homenest.repository.ReviewRepository;
import com.homenest.homenest.repository.UserRepository;
import com.homenest.homenest.service.DiscountService;
import com.homenest.homenest.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ListingService listingService;

    @Autowired
    private DiscountService discountService;

    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // KPIs
        long totalUsers = userRepository.count();
        long totalHosts = userRepository.countByRole("ROLE_HOST");
        long totalGuests = userRepository.countByRole("ROLE_GUEST");
        long totalAdmins = userRepository.countByRole("ROLE_ADMIN");
        long totalListings = listingRepository.count();
        long publishedListings = listingRepository.countByIsPublishedTrue();
        long totalBookings = bookingRepository.count();
        BigDecimal allTimeRevenue = bookingRepository.sumAllRevenue();
        BigDecimal adminCommissionAllTime = bookingRepository.sumAdminCommission();

        // Today's performance
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        long todayBookings = bookingRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(todayStart, todayEnd).size();
        BigDecimal todayRevenue = bookingRepository.sumRevenueBetween(todayStart, todayEnd);
        BigDecimal todayAdminCommission = bookingRepository.sumAdminCommissionBetween(todayStart, todayEnd);
        long activeUsersToday = bookingRepository.countActiveUsersInRange(todayStart, todayEnd);

        // This month revenue
        YearMonth thisMonth = YearMonth.now();
        LocalDateTime monthStart = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = thisMonth.atEndOfMonth().atTime(LocalTime.MAX);
        BigDecimal thisMonthRevenue = bookingRepository.sumRevenueBetween(monthStart, monthEnd);
        BigDecimal thisMonthAdminCommission = bookingRepository.sumAdminCommissionBetween(monthStart, monthEnd);

        // Top performing listings
        List<Object[]> topListingsData = bookingRepository.topListingsByRevenue();
        List<Map<String, Object>> topListings = new ArrayList<>();
        Map<Long, Double> avgRatings = new HashMap<>();
        for (Object[] r : reviewRepository.averageRatingPerListing()) {
            avgRatings.put((Long) r[0], (Double) r[1]);
        }
        int rank = 1;
        for (Object[] row : topListingsData) {
            if (rank > 10)
                break;
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("listingId", row[0]);
            item.put("title", row[1]);
            item.put("city", row[2]);
            item.put("bookingCount", row[3]);
            item.put("revenue", row[4]);
            item.put("avgRating", avgRatings.getOrDefault((Long) row[0], 0.0));
            topListings.add(item);
        }

        // Recent transactions
        List<Booking> recentBookings = bookingRepository.findTop10ByOrderByCreatedAtDesc();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalHosts", totalHosts);
        model.addAttribute("totalGuests", totalGuests);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalListings", totalListings);
        model.addAttribute("publishedListings", publishedListings);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("allTimeRevenue", allTimeRevenue);
        model.addAttribute("adminCommissionAllTime", adminCommissionAllTime);
        model.addAttribute("todayBookings", todayBookings);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("todayAdminCommission", todayAdminCommission);
        model.addAttribute("activeUsersToday", activeUsersToday);
        model.addAttribute("thisMonthRevenue", thisMonthRevenue);
        model.addAttribute("thisMonthAdminCommission", thisMonthAdminCommission);
        model.addAttribute("topListings", topListings);
        model.addAttribute("recentBookings", recentBookings);

        return "admin-dashboard";
    }

    // ========== LISTINGS MANAGEMENT ==========
    @GetMapping("/listings")
    public String listings(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {
        List<Listing> allListings = listingService.findAll();

        // Filter
        List<Listing> filtered = allListings.stream()
                .filter(l -> city == null || city.isEmpty() || l.getCity().toLowerCase().contains(city.toLowerCase()))
                .filter(l -> published == null || l.getIsPublished().equals(published))
                .filter(l -> minPrice == null || l.getPricePerNight().compareTo(minPrice) >= 0)
                .filter(l -> maxPrice == null || l.getPricePerNight().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        long total = listingRepository.count();
        long publishedCount = listingRepository.countByIsPublishedTrue();
        long unpublishedCount = total - publishedCount;

        model.addAttribute("listings", filtered);
        model.addAttribute("totalListings", total);
        model.addAttribute("publishedListings", publishedCount);
        model.addAttribute("unpublishedListings", unpublishedCount);
        model.addAttribute("city", city);
        model.addAttribute("published", published != null ? published.toString() : "");
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "admin-listings";
    }

    @PostMapping("/listings/{id}/toggle-publish")
    public String togglePublish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Listing> opt = listingService.findById(id);
        if (opt.isPresent()) {
            Listing listing = opt.get();
            listing.setIsPublished(!listing.getIsPublished());
            listingRepository.save(listing);
            redirectAttributes.addFlashAttribute("success", "Listing publish status updated.");
        }
        return "redirect:/admin/listings";
    }

    @PostMapping("/listings/{id}/delete")
    public String deleteListing(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        listingService.deleteListing(id);
        redirectAttributes.addFlashAttribute("success", "Listing deleted successfully.");
        return "redirect:/admin/listings";
    }

    // ========== USER MANAGEMENT ==========
    @GetMapping("/users")
    public String users(Model model) {
        List<User> allUsers = userRepository.findAll();
        long total = userRepository.count();
        long hosts = userRepository.countByRole("ROLE_HOST");
        long guests = userRepository.countByRole("ROLE_GUEST");
        long admins = userRepository.countByRole("ROLE_ADMIN");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newUsers = userRepository.countUsersCreatedSince(thirtyDaysAgo);

        model.addAttribute("users", allUsers);
        model.addAttribute("totalUsers", total);
        model.addAttribute("totalHosts", hosts);
        model.addAttribute("totalGuests", guests);
        model.addAttribute("totalAdmins", admins);
        model.addAttribute("newUsers", newUsers);

        return "admin-users";
    }

    @PostMapping("/users/{id}/promote")
    public String promoteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setRole("ROLE_HOST");
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "User promoted to HOST.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/demote")
    public String demoteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setRole("ROLE_GUEST");
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "User demoted to GUEST.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    // ========== TRANSACTIONS ==========
    @GetMapping("/transactions")
    public String transactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay()
                : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        BigDecimal totalRevenue = bookingRepository.sumRevenueBetween(start, end);
        long totalTransactions = bookings.size();
        BigDecimal avgBookingValue = totalTransactions > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        model.addAttribute("bookings", bookings);
        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("avgBookingValue", avgBookingValue);
        model.addAttribute("startDate", startDate != null ? startDate : LocalDate.now().minusDays(30));
        model.addAttribute("endDate", endDate != null ? endDate : LocalDate.now());

        return "admin-transactions";
    }

    @GetMapping("/transactions/export")
    public ResponseEntity<String> exportTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay()
                : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Date,Guest,Host,Listing,Nights,Amount,Status\n");
        for (Booking b : bookings) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(b.getStartDate(), b.getEndDate());
            csv.append(b.getId()).append(",")
                    .append(b.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(",")
                    .append(b.getGuest().getUsername()).append(",")
                    .append(b.getHost().getUsername()).append(",")
                    .append("\"").append(b.getListing().getTitle()).append("\"").append(",")
                    .append(nights).append(",")
                    .append(b.getTotalAmount()).append(",")
                    .append(b.getBookingStatus()).append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    // ========== SALES REPORT ==========
    @GetMapping("/reports/sales")
    public String salesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay()
                : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        BigDecimal totalRevenue = bookingRepository.sumRevenueBetween(start, end);
        long totalBookings = bookings.size();
        BigDecimal avgRevenue = totalBookings > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Revenue by city
        List<Object[]> revenueByCityData = bookingRepository.revenueByCity(start, end);
        List<Map<String, Object>> revenueByCity = new ArrayList<>();
        for (Object[] row : revenueByCityData) {
            Map<String, Object> item = new HashMap<>();
            item.put("city", row[0]);
            item.put("bookingCount", row[1]);
            item.put("revenue", row[2]);
            revenueByCity.add(item);
        }

        // Top listings by revenue in range
        Map<Long, BigDecimal> revenuePerListing = new HashMap<>();
        Map<Long, Long> countPerListing = new HashMap<>();
        Map<Long, String> listingTitles = new HashMap<>();
        Map<Long, String> listingCities = new HashMap<>();
        for (Booking b : bookings) {
            Long lid = b.getListing().getId();
            revenuePerListing.put(lid, revenuePerListing.getOrDefault(lid, BigDecimal.ZERO).add(b.getTotalAmount()));
            countPerListing.put(lid, countPerListing.getOrDefault(lid, 0L) + 1);
            listingTitles.putIfAbsent(lid, b.getListing().getTitle());
            listingCities.putIfAbsent(lid, b.getListing().getCity());
        }

        List<Map<String, Object>> topListingsInRange = revenuePerListing.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("listingId", e.getKey());
                    item.put("title", listingTitles.get(e.getKey()));
                    item.put("city", listingCities.get(e.getKey()));
                    item.put("revenue", e.getValue());
                    item.put("bookingCount", countPerListing.get(e.getKey()));
                    return item;
                })
                .collect(Collectors.toList());

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("avgRevenue", avgRevenue);
        model.addAttribute("revenueByCity", revenueByCity);
        model.addAttribute("topListings", topListingsInRange);
        model.addAttribute("startDate", startDate != null ? startDate : LocalDate.now().minusDays(30));
        model.addAttribute("endDate", endDate != null ? endDate : LocalDate.now());

        return "admin-sales-report";
    }

    // ========== DISCOUNTS ==========
    @GetMapping("/discounts")
    public String discounts(Model model) {
        List<Discount> discounts = discountService.findAll();
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Discount d : discounts) {
            Map<String, Object> item = new HashMap<>();
            item.put("discount", d);
            String status;
            if (!d.isActive()) {
                status = "INACTIVE";
            } else if (d.getStartDate() != null && today.isBefore(d.getStartDate())) {
                status = "SCHEDULED";
            } else if (d.getEndDate() != null && today.isAfter(d.getEndDate())) {
                status = "EXPIRED";
            } else {
                status = "ACTIVE";
            }
            item.put("status", status);
            enriched.add(item);
        }

        model.addAttribute("discounts", enriched);
        return "admin-discounts";
    }

    @GetMapping("/discounts/new")
    public String newDiscount(Model model) {
        model.addAttribute("discount", new Discount());
        model.addAttribute("isEdit", false);
        return "admin-discount-form";
    }

    @PostMapping("/discounts")
    public String createDiscount(@ModelAttribute Discount discount, RedirectAttributes redirectAttributes) {
        discountService.save(discount);
        redirectAttributes.addFlashAttribute("success", "Discount created successfully.");
        return "redirect:/admin/discounts";
    }

    @GetMapping("/discounts/{id}/edit")
    public String editDiscount(@PathVariable Long id, Model model) {
        Optional<Discount> opt = discountService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/admin/discounts";
        }
        model.addAttribute("discount", opt.get());
        model.addAttribute("isEdit", true);
        return "admin-discount-form";
    }

    @PostMapping("/discounts/{id}")
    public String updateDiscount(@PathVariable Long id, @ModelAttribute Discount discount,
            RedirectAttributes redirectAttributes) {
        discount.setId(id);
        discountService.save(discount);
        redirectAttributes.addFlashAttribute("success", "Discount updated successfully.");
        return "redirect:/admin/discounts";
    }

    @PostMapping("/discounts/{id}/delete")
    public String deleteDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        discountService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Discount deleted successfully.");
        return "redirect:/admin/discounts";
    }
}
