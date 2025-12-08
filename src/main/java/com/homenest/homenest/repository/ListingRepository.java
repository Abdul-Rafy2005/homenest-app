package com.homenest.homenest.repository;

import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

        List<Listing> findByIsPublishedTrue();

        long countByIsPublishedTrue();

        List<Listing> findByHost(User host);

        List<Listing> findByCityContainingIgnoreCase(String city);

        @Query("SELECT l FROM Listing l WHERE l.isPublished = true " +
                        "AND (:city IS NULL OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
                        "AND (:minPrice IS NULL OR l.pricePerNight >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR l.pricePerNight <= :maxPrice)")
        List<Listing> searchListings(@Param("city") String city,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice);

        @Query("SELECT l FROM Listing l LEFT JOIN Review r ON r.listing = l " +
                        "WHERE l.isPublished = true " +
                        "GROUP BY l " +
                        "ORDER BY COALESCE(AVG(r.rating),0) DESC, l.pricePerNight ASC, l.createdAt DESC")
        List<Listing> findRecommended();
}
