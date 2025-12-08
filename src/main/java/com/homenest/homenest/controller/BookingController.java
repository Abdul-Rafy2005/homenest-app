package com.homenest.homenest.controller;

import com.homenest.homenest.exception.BookingConflictException;
import com.homenest.homenest.model.Booking;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.service.BookingService;
import com.homenest.homenest.service.ListingService;
import com.homenest.homenest.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ListingService listingService;

    @GetMapping("/create")
    public String showBookingForm(@RequestParam Long listingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        Optional<Listing> listingOpt = listingService.findById(listingId);
        if (listingOpt.isEmpty()) {
            return "redirect:/";
        }

        Listing listing = listingOpt.get();

        // Validate date range before showing the confirmation page
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            redirectAttributes.addFlashAttribute("errorMessage", "End date must be after start date");
            return "redirect:/listings/" + listingId;
        }

        // Pre-check availability and short-circuit if the dates are already booked
        if (bookingService.hasDateConflict(listingId, startDate, endDate)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "The selected dates are not available for this property.");
            redirectAttributes.addFlashAttribute("conflictStartDate", startDate);
            redirectAttributes.addFlashAttribute("conflictEndDate", endDate);
            return "redirect:/listings/" + listingId;
        }
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal totalAmount = listing.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        model.addAttribute("listing", listing);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("nights", nights);
        model.addAttribute("totalAmount", totalAmount);

        return "booking-create";
    }

    @PostMapping
    public String createBooking(@RequestParam Long listingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            Booking booking = bookingService.createBooking(listingId, currentUser.getUser(), startDate, endDate);
            model.addAttribute("booking", booking);
            return "booking-success";
        } catch (BookingConflictException e) {
            // Handle booking conflict - redirect back to listing with error message
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("conflictStartDate", startDate);
            redirectAttributes.addFlashAttribute("conflictEndDate", endDate);
            return "redirect:/listings/" + listingId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/listings/" + listingId;
        }
    }

    @GetMapping("/guest/bookings")
    public String guestBookings(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingService.findByGuest(currentUser.getUser());
        model.addAttribute("bookings", bookings);
        model.addAttribute("currentUser", currentUser.getUser());

        return "guest-bookings";
    }

    @GetMapping("/host/bookings")
    public String hostBookings(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Only hosts should see this page; security layer already restricts, but
        // double-check.
        if (currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_HOST"))) {
            model.addAttribute("error", "You must be a host to view booking requests.");
            return "host-bookings";
        }

        List<Booking> bookings = bookingService.findByHost(currentUser.getUser());
        model.addAttribute("bookings", bookings);
        model.addAttribute("currentUser", currentUser.getUser());

        return "host-bookings";
    }
}
