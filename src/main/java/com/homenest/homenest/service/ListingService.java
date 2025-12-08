package com.homenest.homenest.service;

import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    public Listing createListing(Listing listing) {
        return listingRepository.save(listing);
    }

    public Listing updateListing(Listing listing) {
        return listingRepository.save(listing);
    }

    public void deleteListing(Long id) {
        listingRepository.deleteById(id);
    }

    public Optional<Listing> findById(Long id) {
        return listingRepository.findById(id);
    }

    public List<Listing> findAll() {
        return listingRepository.findAll();
    }

    public List<Listing> findAllPublished() {
        return listingRepository.findByIsPublishedTrue();
    }

    public List<Listing> findByHost(User host) {
        return listingRepository.findByHost(host);
    }

    public List<Listing> searchByCity(String city) {
        return listingRepository.findByCityContainingIgnoreCase(city);
    }

    public List<Listing> searchListings(String city, BigDecimal minPrice, BigDecimal maxPrice) {
        return listingRepository.searchListings(city, minPrice, maxPrice);
    }

    public List<Listing> findRecommended() {
        return listingRepository.findRecommended();
    }
}
