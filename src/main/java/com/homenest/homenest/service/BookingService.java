package com.homenest.homenest.service;

import com.homenest.homenest.exception.BookingConflictException;
import com.homenest.homenest.model.Booking;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ListingService listingService;

    public Booking createBooking(Long listingId, User guest, LocalDate startDate, LocalDate endDate) {
        // Basic validation
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Optional<Listing> listingOpt = listingService.findById(listingId);
        if (listingOpt.isEmpty()) {
            throw new IllegalArgumentException("Listing not found");
        }

        Listing listing = listingOpt.get();

        // Check for booking conflicts
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                listingId, startDate, endDate);

        if (!overlappingBookings.isEmpty()) {
            throw new BookingConflictException(
                    "The selected dates are not available. This property is already booked for those dates.");
        }

        // Calculate total amount
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal totalAmount = listing.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setListing(listing);
        booking.setGuest(guest);
        booking.setHost(listing.getHost());
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus("PENDING");

        // Calculate admin commission (5% by default)
        BigDecimal commissionPercentage = new BigDecimal("5.00");
        BigDecimal adminCommission = totalAmount.multiply(commissionPercentage).divide(BigDecimal.valueOf(100));
        booking.setCommissionPercentage(commissionPercentage);
        booking.setAdminCommission(adminCommission);

        return bookingRepository.save(booking);
    }

    /**
     * Check if a listing has any booking conflicts for the given date range.
     * Useful for availability checking before attempting to book.
     */
    public boolean hasDateConflict(Long listingId, LocalDate startDate, LocalDate endDate) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                listingId, startDate, endDate);
        return !overlappingBookings.isEmpty();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> findByGuest(User guest) {
        return bookingRepository.findByGuestOrderByCreatedAtDesc(guest);
    }

    public List<Booking> findByHost(User host) {
        return bookingRepository.findByHostOrderByCreatedAtDesc(host);
    }

    public List<Booking> findByListing(Listing listing) {
        return bookingRepository.findByListing(listing);
    }

    public Booking updateBookingStatus(Long bookingId, String status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found");
        }

        Booking booking = bookingOpt.get();
        booking.setBookingStatus(status);
        return bookingRepository.save(booking);
    }
}
