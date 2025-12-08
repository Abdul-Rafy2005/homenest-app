package com.homenest.homenest.repository;

import com.homenest.homenest.model.Booking;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

        List<Booking> findByGuest(User guest);

        List<Booking> findByHost(User host);

        List<Booking> findByListing(Listing listing);

        List<Booking> findByGuestAndListingAndBookingStatusIn(User guest, Listing listing, List<String> statuses);

        List<Booking> findByGuestOrderByCreatedAtDesc(User guest);

        List<Booking> findByHostOrderByCreatedAtDesc(User host);

        List<Booking> findTop10ByOrderByCreatedAtDesc();

        List<Booking> findByCreatedAtBetweenOrderByCreatedAtDesc(java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        @Query("SELECT COALESCE(SUM(b.totalAmount),0) FROM Booking b")
        java.math.BigDecimal sumAllRevenue();

        @Query("SELECT COALESCE(SUM(b.totalAmount),0) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end")
        java.math.BigDecimal sumRevenueBetween(@Param("start") java.time.LocalDateTime start,
                        @Param("end") java.time.LocalDateTime end);

        @Query("SELECT COUNT(DISTINCT b.guest.id) + COUNT(DISTINCT b.host.id) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end")
        long countActiveUsersInRange(@Param("start") java.time.LocalDateTime start,
                        @Param("end") java.time.LocalDateTime end);

        @Query("SELECT b.listing.city, COUNT(b), COALESCE(SUM(b.totalAmount),0) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end GROUP BY b.listing.city ORDER BY SUM(b.totalAmount) DESC")
        List<Object[]> revenueByCity(@Param("start") java.time.LocalDateTime start,
                        @Param("end") java.time.LocalDateTime end);

        @Query("SELECT b.listing.id, b.listing.title, b.listing.city, COUNT(b), COALESCE(SUM(b.totalAmount),0) FROM Booking b GROUP BY b.listing.id, b.listing.title, b.listing.city ORDER BY SUM(b.totalAmount) DESC")
        List<Object[]> topListingsByRevenue();

        @Query("SELECT b.listing.id, COUNT(b) FROM Booking b GROUP BY b.listing.id")
        List<Object[]> bookingCountPerListing();

        @Query("SELECT COALESCE(SUM(b.adminCommission),0) FROM Booking b")
        java.math.BigDecimal sumAdminCommission();

        @Query("SELECT COALESCE(SUM(b.adminCommission),0) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end")
        java.math.BigDecimal sumAdminCommissionBetween(@Param("start") java.time.LocalDateTime start,
                        @Param("end") java.time.LocalDateTime end);

        @Query("SELECT COALESCE(SUM(b.totalAmount - b.adminCommission),0) FROM Booking b WHERE b.host = :host")
        java.math.BigDecimal sumHostEarningsAfterCommission(@Param("host") User host);

        /**
         * Find bookings for a listing that overlap with the given date range.
         * Overlap occurs when:
         * - Existing booking starts before new end date AND
         * - Existing booking ends after new start date
         */
        @Query("SELECT b FROM Booking b WHERE b.listing.id = :listingId " +
                        "AND b.bookingStatus NOT IN ('CANCELLED') " +
                        "AND b.startDate < :endDate " +
                        "AND b.endDate > :startDate")
        List<Booking> findOverlappingBookings(@Param("listingId") Long listingId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}
