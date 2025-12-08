package com.homenest.homenest.repository;

import com.homenest.homenest.model.Review;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByListing(Listing listing);

    List<Review> findByListingOrderByCreatedAtDesc(Listing listing);

    boolean existsByListingAndGuest(Listing listing, com.homenest.homenest.model.User guest);

    List<Review> findByListingHostOrderByCreatedAtDesc(User host);

    @Query("SELECT r.listing.id, AVG(r.rating) FROM Review r GROUP BY r.listing.id")
    List<Object[]> averageRatingPerListing();
}
