package com.homenest.homenest.controller;

import com.homenest.homenest.model.Booking;
import com.homenest.homenest.model.Discount;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.Review;
import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.BookingRepository;
import com.homenest.homenest.repository.DiscountRepository;
import com.homenest.homenest.repository.ListingRepository;
import com.homenest.homenest.repository.ReviewRepository;
import com.homenest.homenest.service.DiscountService;
import com.homenest.homenest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/host")
@PreAuthorize("hasRole('HOST')")
public class HostController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    private static final String UPLOAD_DIR = "uploads/listings";

    private User getCurrentHost(Authentication auth) {
        String username = auth.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID() + extension;

        // Save file
        Path filepath = uploadPath.resolve(filename);
        Files.write(filepath, file.getBytes());

        // Return relative path for storing in database
        return "/" + UPLOAD_DIR + "/" + filename;
    }

    @GetMapping("/dashboard")
    public String hostDashboard(Authentication auth, Model model) {
        User host = getCurrentHost(auth);

        // Get host's listings
        List<Listing> allListings = listingRepository.findByHost(host);
        long totalListings = allListings.size();
        long activeListings = allListings.stream().filter(Listing::getIsPublished).count();

        // Get host's bookings (where this user is the host)
        List<Booking> allBookings = bookingRepository.findByHostOrderByCreatedAtDesc(host);
        long totalBookings = allBookings.size();

        // Calculate total earnings (CONFIRMED + COMPLETED bookings) AFTER commission
        // deduction
        BigDecimal totalEarnings = allBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getBookingStatus()) || "COMPLETED".equals(b.getBookingStatus()))
                .map(b -> b.getTotalAmount().subtract(b.getAdminCommission()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total commission paid to admin
        BigDecimal totalCommissionPaid = allBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getBookingStatus()) || "COMPLETED".equals(b.getBookingStatus()))
                .map(Booking::getAdminCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get top 5 listings
        List<Listing> topListings = allListings.stream().limit(5).toList();

        // Get recent bookings (last 5)
        List<Booking> recentBookings = allBookings.stream().limit(5).toList();

        model.addAttribute("host", host);
        model.addAttribute("totalListings", totalListings);
        model.addAttribute("activeListings", activeListings);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("totalCommissionPaid", totalCommissionPaid);
        model.addAttribute("topListings", topListings);
        model.addAttribute("recentBookings", recentBookings);

        return "host-dashboard";
    }

    @GetMapping("/listings")
    public String hostListings(Authentication auth, Model model) {
        User host = getCurrentHost(auth);
        List<Listing> listings = listingRepository.findByHost(host);
        model.addAttribute("listings", listings);
        model.addAttribute("host", host);
        return "host-listings";
    }

    @GetMapping("/listings/new")
    public String newListingForm(Authentication auth, Model model) {
        User host = getCurrentHost(auth);
        model.addAttribute("listing", new Listing());
        model.addAttribute("host", host);
        model.addAttribute("isEdit", false);
        return "host-listing-form";
    }

    @PostMapping("/listings")
    public String createListing(@ModelAttribute Listing listing,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            User host = getCurrentHost(auth);
            listing.setHost(host);

            // Handle image upload
            if (mainImage != null && !mainImage.isEmpty()) {
                String imageUrl = saveUploadedFile(mainImage);
                listing.setImageUrl(imageUrl);
            }

            listingRepository.save(listing);
            redirectAttributes.addFlashAttribute("success", "Listing created successfully!");
            return "redirect:/host/listings";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
            return "redirect:/host/listings/new";
        }
    }

    @GetMapping("/listings/{id}/edit")
    public String editListingForm(@PathVariable Long id,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {
        User host = getCurrentHost(auth);
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Ensure this listing belongs to the current host
        if (!listing.getHost().getId().equals(host.getId())) {
            redirectAttributes.addFlashAttribute("error", "You can only edit your own listings");
            return "redirect:/host/listings";
        }

        model.addAttribute("listing", listing);
        model.addAttribute("host", host);
        model.addAttribute("isEdit", true);
        return "host-listing-form";
    }

    @PostMapping("/listings/{id}")
    public String updateListing(@PathVariable Long id,
            @ModelAttribute Listing updatedListing,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            User host = getCurrentHost(auth);
            Listing listing = listingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Listing not found"));

            // Ensure this listing belongs to the current host
            if (!listing.getHost().getId().equals(host.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only edit your own listings");
                return "redirect:/host/listings";
            }

            listing.setTitle(updatedListing.getTitle());
            listing.setDescription(updatedListing.getDescription());
            listing.setPricePerNight(updatedListing.getPricePerNight());
            listing.setCity(updatedListing.getCity());
            listing.setAddress(updatedListing.getAddress());
            listing.setPropertyType(updatedListing.getPropertyType());
            listing.setCapacity(updatedListing.getCapacity());
            listing.setIsPublished(updatedListing.getIsPublished());

            // Handle image upload
            if (mainImage != null && !mainImage.isEmpty()) {
                String imageUrl = saveUploadedFile(mainImage);
                listing.setImageUrl(imageUrl);
            }

            listingRepository.save(listing);
            redirectAttributes.addFlashAttribute("success", "Listing updated successfully!");
            return "redirect:/host/listings";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
            return "redirect:/host/listings/" + id + "/edit";
        }
    }

    @PostMapping("/listings/{id}/delete")
    public String deleteListing(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        User host = getCurrentHost(auth);
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Ensure this listing belongs to the current host
        if (!listing.getHost().getId().equals(host.getId())) {
            redirectAttributes.addFlashAttribute("error", "You can only delete your own listings");
            return "redirect:/host/listings";
        }

        listingRepository.delete(listing);
        redirectAttributes.addFlashAttribute("success", "Listing deleted successfully!");
        return "redirect:/host/listings";
    }

    @GetMapping("/bookings")
    public String hostBookings(Authentication auth, Model model) {
        User host = getCurrentHost(auth);
        List<Booking> bookings = bookingRepository.findByHostOrderByCreatedAtDesc(host);

        // Calculate stats
        LocalDate today = LocalDate.now();
        long futureBookings = bookings.stream()
                .filter(b -> b.getStartDate().isAfter(today))
                .count();
        long completedBookings = bookings.stream()
                .filter(b -> "COMPLETED".equals(b.getBookingStatus()))
                .count();
        BigDecimal totalEarnings = bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getBookingStatus()) || "COMPLETED".equals(b.getBookingStatus()))
                .map(b -> b.getTotalAmount().subtract(b.getAdminCommission()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommission = bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getBookingStatus()) || "COMPLETED".equals(b.getBookingStatus()))
                .map(Booking::getAdminCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("bookings", bookings);
        model.addAttribute("host", host);
        model.addAttribute("futureBookings", futureBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("totalCommission", totalCommission);
        return "host-bookings";
    }

    @GetMapping("/reviews")
    public String hostReviews(Authentication auth, Model model) {
        User host = getCurrentHost(auth);
        List<Review> reviews = reviewRepository.findByListingHostOrderByCreatedAtDesc(host);
        model.addAttribute("reviews", reviews);
        model.addAttribute("host", host);
        return "host-reviews";
    }

    @PostMapping("/bookings/{id}/confirm")
    public String confirmBooking(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        User host = getCurrentHost(auth);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ensure this booking belongs to the current host
        if (!booking.getHost().getId().equals(host.getId())) {
            redirectAttributes.addFlashAttribute("error", "You can only confirm your own bookings");
            return "redirect:/host/bookings";
        }

        booking.setBookingStatus("CONFIRMED");
        bookingRepository.save(booking);
        redirectAttributes.addFlashAttribute("success", "Booking confirmed successfully!");
        return "redirect:/host/bookings";
    }

    @PostMapping("/bookings/{id}/reject")
    public String rejectBooking(@PathVariable Long id,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        User host = getCurrentHost(auth);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ensure this booking belongs to the current host
        if (!booking.getHost().getId().equals(host.getId())) {
            redirectAttributes.addFlashAttribute("error", "You can only reject your own bookings");
            return "redirect:/host/bookings";
        }

        booking.setBookingStatus("REJECTED");
        bookingRepository.save(booking);
        redirectAttributes.addFlashAttribute("success", "Booking rejected successfully!");
        return "redirect:/host/bookings";
    }

    @GetMapping("/discounts")
    public String hostDiscounts(Model model, Authentication authentication) {
        User host = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Discount> discounts = discountRepository.findByHost(host);
        model.addAttribute("discounts", discounts);
        return "host-discount-list";
    }

    @GetMapping("/discounts/new")
    public String newDiscountForm(Model model, Authentication authentication) {
        User host = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Listing> hostListings = listingRepository.findByHost(host);
        model.addAttribute("discount", new Discount());
        model.addAttribute("listings", hostListings);
        model.addAttribute("isEdit", false);
        return "host-discount-form";
    }

    @PostMapping("/discounts")
    public String createDiscount(@ModelAttribute Discount discount,
            @RequestParam(value = "selectedListings", required = false) List<Long> selectedListings,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User host = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            discount.setHost(host);

            // Set selected listings
            if (selectedListings != null && !selectedListings.isEmpty()) {
                List<Listing> listings = listingRepository.findAllById(selectedListings);
                discount.setListings(listings);
            } else {
                discount.setListings(List.of());
            }

            discountRepository.save(discount);
            redirectAttributes.addFlashAttribute("success", "Discount created successfully!");
            return "redirect:/host/discounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create discount: " + e.getMessage());
            return "redirect:/host/discounts/new";
        }
    }

    @GetMapping("/discounts/{id}/edit")
    public String editDiscountForm(@PathVariable Long id, Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User host = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Discount discount = discountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Discount not found"));

            // Verify ownership
            if (!discount.getHost().getId().equals(host.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access!");
                return "redirect:/host/discounts";
            }

            List<Listing> hostListings = listingRepository.findByHost(host);
            model.addAttribute("discount", discount);
            model.addAttribute("listings", hostListings);
            model.addAttribute("selectedListingIds", discount.getListings().stream()
                    .map(Listing::getId)
                    .toList());
            model.addAttribute("isEdit", true);
            return "host-discount-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to load discount: " + e.getMessage());
            return "redirect:/host/discounts";
        }
    }

    @PostMapping("/discounts/{id}")
    public String updateDiscount(@PathVariable Long id,
            @ModelAttribute Discount updatedDiscount,
            @RequestParam(value = "selectedListings", required = false) List<Long> selectedListings,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User host = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Discount discount = discountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Discount not found"));

            // Verify ownership
            if (!discount.getHost().getId().equals(host.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access!");
                return "redirect:/host/discounts";
            }

            discount.setName(updatedDiscount.getName());
            discount.setDescription(updatedDiscount.getDescription());
            discount.setPercentage(updatedDiscount.getPercentage());
            discount.setStartDate(updatedDiscount.getStartDate());
            discount.setEndDate(updatedDiscount.getEndDate());
            discount.setActive(updatedDiscount.isActive());

            // Update selected listings
            if (selectedListings != null && !selectedListings.isEmpty()) {
                List<Listing> listings = listingRepository.findAllById(selectedListings);
                discount.setListings(listings);
            } else {
                discount.setListings(List.of());
            }

            discountRepository.save(discount);
            redirectAttributes.addFlashAttribute("success", "Discount updated successfully!");
            return "redirect:/host/discounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update discount: " + e.getMessage());
            return "redirect:/host/discounts/" + id + "/edit";
        }
    }

    @PostMapping("/discounts/{id}/delete")
    public String deleteDiscount(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User host = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Discount discount = discountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Discount not found"));

            // Verify ownership
            if (!discount.getHost().getId().equals(host.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access!");
                return "redirect:/host/discounts";
            }

            discountRepository.delete(discount);
            redirectAttributes.addFlashAttribute("success", "Discount deleted successfully!");
            return "redirect:/host/discounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete discount: " + e.getMessage());
            return "redirect:/host/discounts";
        }
    }
}
