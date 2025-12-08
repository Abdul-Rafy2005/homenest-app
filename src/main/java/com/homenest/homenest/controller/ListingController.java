package com.homenest.homenest.controller;

import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.Review;
import com.homenest.homenest.service.ListingService;
import com.homenest.homenest.security.CustomUserDetails;
import com.homenest.homenest.repository.ReviewRepository;
import com.homenest.homenest.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/{id}")
    public String viewListing(@PathVariable Long id,
            Model model,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Listing> listingOpt = listingService.findById(id);
        if (listingOpt.isEmpty()) {
            return "redirect:/";
        }

        Listing listing = listingOpt.get();

        // Reviews data
        var reviews = reviewRepository.findByListingOrderByCreatedAtDesc(listing);
        double averageRating = reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(r -> r.getRating() == null ? 0 : r.getRating()).average().orElse(0.0);

        boolean canReview = false;
        boolean alreadyReviewed = false;
        if (currentUser != null) {
            alreadyReviewed = reviewRepository.existsByListingAndGuest(listing, currentUser.getUser());
            var eligibleStatuses = java.util.Arrays.asList("COMPLETED");
            var eligibleBookings = bookingRepository.findByGuestAndListingAndBookingStatusIn(
                    currentUser.getUser(), listing, eligibleStatuses);
            canReview = !alreadyReviewed && !eligibleBookings.isEmpty();
        }

        model.addAttribute("listing", listing);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("canReview", canReview);
        model.addAttribute("alreadyReviewed", alreadyReviewed);
        return "listing-detail";
    }

    @GetMapping("/host/listings")
    public String hostListings(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_HOST"))) {
            model.addAttribute("error", "You must be a host to view your listings.");
            return "host-listings";
        }

        List<Listing> listings = listingService.findByHost(currentUser.getUser());
        model.addAttribute("listings", listings);
        model.addAttribute("currentUser", currentUser.getUser());
        return "host-listings";
    }

    @GetMapping("/host/listings/new")
    public String newListingForm(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_HOST"))) {
            model.addAttribute("error", "Only hosts can create listings.");
            return "host-listings";
        }
        model.addAttribute("listing", new Listing());
        return "listing-form";
    }

    @PostMapping("/host/listings")
    public String createListing(@ModelAttribute Listing listing,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_HOST"))) {
            redirectAttributes.addFlashAttribute("error", "Only hosts can create listings.");
            return "redirect:/listings/host/listings";
        }

        listing.setHost(currentUser.getUser());
        listingService.createListing(listing);

        return "redirect:/listings/host/listings";
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable Long id,
            @RequestParam int rating,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Listing listing = listingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating must be between 1 and 5");
            return "redirect:/listings/" + id;
        }

        // Ensure guest has an eligible booking
        var eligibleStatuses = java.util.Arrays.asList("COMPLETED");
        var eligibleBookings = bookingRepository.findByGuestAndListingAndBookingStatusIn(
                currentUser.getUser(), listing, eligibleStatuses);
        if (eligibleBookings.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You can only review after the stay is completed.");
            return "redirect:/listings/" + id;
        }

        if (reviewRepository.existsByListingAndGuest(listing, currentUser.getUser())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this property.");
            return "redirect:/listings/" + id;
        }

        Review review = new Review();
        review.setListing(listing);
        review.setGuest(currentUser.getUser());
        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("success", "Thanks for reviewing this stay!");
        return "redirect:/listings/" + id;
    }
}
