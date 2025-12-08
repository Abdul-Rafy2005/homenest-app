package com.homenest.homenest.service;

import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.ListingRepository;
import com.homenest.homenest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for importing listings from external APIs (e.g., Hostelworld,
 * Booking.com, custom hostel APIs).
 * 
 * CONFIGURATION REQUIRED:
 * 
 * 1. Add these properties to application.properties:
 * homenest.external.api.enabled=false
 * homenest.external.api.base-url=https://api.example-hostel-service.com/v1
 * homenest.external.api.key=your-api-key-here
 * homenest.external.api.host-user=host1
 * 
 * 2. Replace or extend the mapExternalListingToEntity() method to match your
 * API response structure.
 * 
 * 3. Call importListingsFromExternalAPI() from a scheduled task or admin
 * endpoint (NOT automatically on startup).
 * 
 * EXAMPLE EXTERNAL API RESPONSE:
 * {
 * "listings": [
 * {
 * "name": "City Backpackers",
 * "description": "Budget-friendly hostel in city center",
 * "location": {"city": "Lahore", "address": "123 Mall Road"},
 * "price_per_night": 1500,
 * "capacity": 12,
 * "type": "HOSTEL"
 * }
 * ]
 * }
 */
@Service
public class ExternalListingImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalListingImportService.class);

    @Value("${homenest.external.api.enabled:false}")
    private boolean apiEnabled;

    @Value("${homenest.external.api.base-url:}")
    private String apiBaseUrl;

    @Value("${homenest.external.api.key:}")
    private String apiKey;

    @Value("${homenest.external.api.host-user:host1}")
    private String hostUsername;

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public ExternalListingImportService(ListingRepository listingRepository,
            UserRepository userRepository,
            RestTemplateBuilder builder) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        // Configure RestTemplate with timeout settings
        this.restTemplate = builder
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .readTimeout(java.time.Duration.ofSeconds(30))
                .build();
    }

    /**
     * Imports listings from external API and saves them to the database.
     * This method should be called from an admin endpoint or scheduled task, NOT
     * automatically.
     * 
     * Usage example in a controller:
     * @PostMapping("/admin/import-external-listings")
     * @PreAuthorize("hasRole('ADMIN')")
     * public ResponseEntity<String> importListings() {
     * try {
     * int imported = externalListingImportService.importListingsFromExternalAPI();
     * return ResponseEntity.ok("Imported " + imported + " listings");
     * } catch (Exception e) {
     * return ResponseEntity.status(500).body("Import failed: " + e.getMessage());
     * }
     * }
     */
    public int importListingsFromExternalAPI() {
        if (!apiEnabled) {
            logger.info("External API import is disabled. Enable by setting homenest.external.api.enabled=true");
            return 0;
        }

        if (apiBaseUrl == null || apiBaseUrl.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            logger.error(
                    "External API not properly configured. Set homenest.external.api.base-url and homenest.external.api.key");
            return 0;
        }

        try {
            User hostUser = userRepository.findByUsername(hostUsername)
                    .orElseThrow(() -> new IllegalArgumentException("Host user '" + hostUsername + "' not found"));

            logger.info("Fetching listings from external API: {}", apiBaseUrl);

            List<Map<String, Object>> externalListings = callExternalAPI();

            int importedCount = 0;
            for (Map<String, Object> externalListing : externalListings) {
                try {
                    Listing listing = mapExternalListingToEntity(externalListing, hostUser);
                    listingRepository.save(listing);
                    importedCount++;
                    logger.debug("Imported listing: {}", listing.getTitle());
                } catch (Exception e) {
                    logger.warn("Failed to import listing: {}", e.getMessage());
                }
            }

            logger.info("Successfully imported {} listings from external API", importedCount);
            return importedCount;

        } catch (Exception e) {
            logger.error("Error importing listings from external API", e);
            throw new RuntimeException("Failed to import external listings: " + e.getMessage(), e);
        }
    }

    /**
     * Maps external API response structure to our Listing entity.
     * CUSTOMIZE THIS METHOD based on your external API's response format.
     * 
     * Example mapping for a generic hostel API:
     */
    private Listing mapExternalListingToEntity(Map<String, Object> externalData, User host) {
        Listing listing = new Listing();
        listing.setHost(host);

        // Map title/name
        listing.setTitle((String) externalData.getOrDefault("name", "Imported Listing"));

        // Map description
        listing.setDescription((String) externalData.getOrDefault("description", ""));

        // Map location
        @SuppressWarnings("unchecked")
        Map<String, Object> location = (Map<String, Object>) externalData.get("location");
        if (location != null) {
            listing.setCity((String) location.getOrDefault("city", "Pakistan"));
            listing.setAddress((String) location.getOrDefault("address", ""));
        }

        // Map pricing (assuming price is in PKR or convert as needed)
        Object price = externalData.get("price_per_night");
        if (price instanceof Number) {
            listing.setPricePerNight(new BigDecimal(((Number) price).doubleValue()));
        }

        // Map capacity
        Object capacity = externalData.get("capacity");
        listing.setCapacity(capacity instanceof Number ? ((Number) capacity).intValue() : 1);

        // Map property type
        listing.setPropertyType((String) externalData.getOrDefault("type", "HOSTEL"));

        // Always import as published
        listing.setIsPublished(true);

        return listing;
    }

    /**
     * EXAMPLE: Method to call Hostelworld-like API (structure shown for reference).
     * Uncomment and adapt if using a real Hostelworld/Booking API.
     * 
     * NOTE: Most public APIs require authentication and may have usage limits.
     */
    private List<Map<String, Object>> callExternalAPI() {
        String url = apiBaseUrl + "/listings?country=PK&api_key=" + apiKey;

        try {
            @SuppressWarnings("rawtypes")
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("listings")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> listings = (List<Map<String, Object>>) response.get("listings");
                return listings;
            }
        } catch (Exception e) {
            logger.error("Failed to call external API", e);
        }

        return new ArrayList<>();
    }

    /**
     * Returns information about external API configuration status.
     */
    public Map<String, Object> getApiStatus() {
        return Map.of(
                "enabled", apiEnabled,
                "configured", apiEnabled && !apiBaseUrl.isEmpty() && !apiKey.isEmpty(),
                "baseUrl", apiBaseUrl,
                "hostUser", hostUsername);
    }
}
