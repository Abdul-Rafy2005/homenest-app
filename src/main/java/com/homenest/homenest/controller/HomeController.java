package com.homenest.homenest.controller;

import com.homenest.homenest.model.Listing;
import com.homenest.homenest.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ListingService listingService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {
        List<Listing> listings;

        List<Listing> recommended = listingService.findRecommended();

        if (city != null || minPrice != null || maxPrice != null) {
            listings = listingService.searchListings(city, minPrice, maxPrice);
        } else {
            listings = recommended;
        }

        model.addAttribute("listings", listings);
        model.addAttribute("recommendedListings", recommended);
        model.addAttribute("searchCity", city);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
