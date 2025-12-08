package com.homenest.homenest.repository;

import com.homenest.homenest.model.Discount;
import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByActiveTrue();

    List<Discount> findByStartDateBeforeAndEndDateAfterAndActiveTrue(LocalDate now1, LocalDate now2);

    List<Discount> findByHost(User host);

    List<Discount> findByHostOrderByCreatedAtDesc(User host);

    List<Discount> findByListingsContaining(Listing listing);

    List<Discount> findByHostAndActiveTrue(User host);
}
