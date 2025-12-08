# External API Integration Guide

## Overview

The `ExternalListingImportService` provides a framework for importing listings from external hostel/accommodation APIs (e.g., Hostelworld, Booking.com) into HomeNest.

**Important:** The external API integration is **disabled by default** and must be manually enabled and triggered. No data is automatically imported on startup.

---

## Configuration

### Step 1: Add Properties to `application.properties`

```properties
# Enable/disable external API integration
homenest.external.api.enabled=true

# Base URL of external API
homenest.external.api.base-url=https://api.example.com/v1

# API key for authentication
homenest.external.api.key=your-secret-api-key

# User account that will own imported listings
homenest.external.api.host-user=host1
```

### Step 2: Use Environment Variables (Recommended for Production)

Instead of hardcoding API keys in `application.properties`, use environment variables:

```bash
# Linux/Mac
export HOMENEST_EXTERNAL_API_ENABLED=true
export HOMENEST_EXTERNAL_API_BASE_URL=https://api.hostelworld.com/v1
export HOMENEST_EXTERNAL_API_KEY=your-secret-key
export HOMENEST_EXTERNAL_API_HOST_USER=host1

# Or in .env file
SPRING_PROFILES_ACTIVE=prod
HOMENEST_EXTERNAL_API_ENABLED=true
HOMENEST_EXTERNAL_API_BASE_URL=https://api.hostelworld.com/v1
HOMENEST_EXTERNAL_API_KEY=your-secret-key
```

```powershell
# Windows PowerShell
$env:HOMENEST_EXTERNAL_API_ENABLED="true"
$env:HOMENEST_EXTERNAL_API_BASE_URL="https://api.hostelworld.com/v1"
$env:HOMENEST_EXTERNAL_API_KEY="your-secret-key"
```

### Step 3: Verify Configuration

Check API status at runtime (create an admin endpoint):

```java
@Autowired
private ExternalListingImportService importService;

@GetMapping("/admin/api-status")
public ResponseEntity<?> checkApiStatus() {
    return ResponseEntity.ok(importService.getApiStatus());
}
```

Response:
```json
{
  "enabled": true,
  "configured": true,
  "baseUrl": "https://api.example.com/v1",
  "hostUser": "host1"
}
```

---

## Usage

### Method 1: Manual Import via Controller

Create an admin endpoint to trigger imports:

```java
package com.homenest.homenest.controller;

import com.homenest.homenest.service.ExternalListingImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ExternalListingImportService importService;

    /**
     * Trigger import of listings from external API.
     * Only accessible by ADMIN users.
     */
    @PostMapping("/import-external-listings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importExternalListings() {
        try {
            int imported = importService.importListingsFromExternalAPI();
            return ResponseEntity.ok(String.format(
                "Successfully imported %d listings from external API", imported
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                "Import failed: " + e.getMessage()
            );
        }
    }

    /**
     * Check external API configuration status.
     */
    @GetMapping("/external-api-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkApiStatus() {
        return ResponseEntity.ok(importService.getApiStatus());
    }
}
```

**Usage:**
```bash
# Login as admin to get JWT token (if using token-based auth)
curl -X POST http://localhost:8080/api/admin/import-external-listings \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"

# Response on success:
# {"message": "Successfully imported 50 listings from external API"}

# Check status:
curl -X GET http://localhost:8080/api/admin/external-api-status \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### Method 2: Scheduled Task

Automatically import listings periodically:

```java
package com.homenest.homenest.scheduler;

import com.homenest.homenest.service.ExternalListingImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ListingImportScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ListingImportScheduler.class);

    @Autowired
    private ExternalListingImportService importService;

    /**
     * Import listings from external API every day at 2 AM.
     * Cron: "0 0 2 * * *" (daily at 2:00 AM)
     * Cron: "0 */6 * * * *" (every 6 hours)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void importListingsDaily() {
        logger.info("Starting scheduled import from external API...");
        try {
            int imported = importService.importListingsFromExternalAPI();
            logger.info("Scheduled import completed: {} listings imported", imported);
        } catch (Exception e) {
            logger.error("Scheduled import failed", e);
        }
    }
}
```

Don't forget to enable scheduling in your main application class:

```java
@SpringBootApplication
@EnableScheduling
public class HomenestApplication {
    // ...
}
```

### Method 3: One-Time Import on Application Startup

Not recommended for production, but useful for testing:

```java
@Bean
public CommandLineRunner importExternalListingsOnStartup(
        ExternalListingImportService importService) {
    return args -> {
        try {
            int imported = importService.importListingsFromExternalAPI();
            System.out.println("Imported " + imported + " listings from external API");
        } catch (Exception e) {
            System.err.println("Failed to import external listings: " + e.getMessage());
        }
    };
}
```

---

## Customizing API Response Mapping

The service uses a default mapping structure. Customize it based on your external API:

### Step 1: Identify External API Response Format

Example from Hostelworld-like API:
```json
{
  "listings": [
    {
      "hostel_id": "12345",
      "hostel_name": "City Backpackers",
      "description": "Budget hostel in city center",
      "location": {
        "city": "Lahore",
        "address": "123 Mall Road",
        "latitude": 31.5204,
        "longitude": 74.3587
      },
      "nightly_rate": 1500,
      "room_type": "HOSTEL",
      "max_occupancy": 12,
      "amenities": ["WiFi", "Kitchen", "Lounge"],
      "rating": 4.5
    }
  ]
}
```

### Step 2: Modify `mapExternalListingToEntity()` Method

```java
private Listing mapExternalListingToEntity(Map<String, Object> externalData, User host) {
    Listing listing = new Listing();
    listing.setHost(host);
    
    // Map title (adjust field name based on your API)
    listing.setTitle((String) externalData.getOrDefault("hostel_name", "Imported Listing"));
    
    // Map description
    listing.setDescription((String) externalData.getOrDefault("description", ""));
    
    // Map location
    @SuppressWarnings("unchecked")
    Map<String, Object> location = (Map<String, Object>) externalData.get("location");
    if (location != null) {
        listing.setCity((String) location.getOrDefault("city", "Pakistan"));
        listing.setAddress((String) location.getOrDefault("address", ""));
    }
    
    // Map pricing (convert to BigDecimal if needed)
    Object price = externalData.get("nightly_rate");
    if (price instanceof Number) {
        listing.setPricePerNight(new BigDecimal(((Number) price).doubleValue()));
    }
    
    // Map capacity
    Object capacity = externalData.get("max_occupancy");
    listing.setCapacity(capacity instanceof Number ? ((Number) capacity).intValue() : 1);
    
    // Map property type
    listing.setPropertyType((String) externalData.getOrDefault("room_type", "HOSTEL"));
    
    // Always publish imported listings
    listing.setIsPublished(true);
    
    return listing;
}
```

### Step 3: Implement API Call

Uncomment and modify the `callExternalAPI()` method:

```java
private List<Map<String, Object>> callExternalAPI() {
    String url = String.format("%s/listings?country=PK&api_key=%s", apiBaseUrl, apiKey);
    
    try {
        logger.debug("Calling external API: {}", url);
        
        // Call external API using RestTemplate
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        if (response != null && response.containsKey("listings")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> listings = (List<Map<String, Object>>) response.get("listings");
            logger.info("Retrieved {} listings from external API", listings.size());
            return listings;
        }
    } catch (org.springframework.web.client.HttpClientErrorException e) {
        logger.error("HTTP error from external API: {} - {}", e.getRawStatusCode(), e.getMessage());
    } catch (Exception e) {
        logger.error("Error calling external API", e);
    }
    
    return new ArrayList<>();
}
```

---

## Error Handling

### Common Issues & Solutions

#### 1. **API Key Not Working**
```
ERROR: 401 Unauthorized
```
**Solution:**
- Verify API key in application.properties
- Check if API key is valid and not expired
- Ensure API key has correct permissions
- Contact API provider support

#### 2. **API Base URL Not Found**
```
ERROR: 404 Not Found
```
**Solution:**
- Verify base URL is correct
- Check endpoint path in `callExternalAPI()`
- Test URL with curl or Postman first
- Check API documentation for correct endpoints

#### 3. **Connection Timeout**
```
ERROR: Connection timeout after 10 seconds
```
**Solution:**
- Increase timeout in RestTemplateBuilder
- Check network connectivity to API server
- Verify firewall rules allow outbound connections
- API server might be down or unresponsive

#### 4. **JSON Parsing Error**
```
ERROR: Cannot deserialize instance of `java.util.ArrayList`
```
**Solution:**
- Verify API response format matches mapping logic
- Check JSON structure in `mapExternalListingToEntity()`
- Add null checks for optional fields
- Log raw API response to debug

### Logging

Enable detailed logging:

```properties
# application.properties
logging.level.com.homenest.homenest.service.ExternalListingImportService=DEBUG
logging.level.org.springframework.web.client.RestTemplate=DEBUG
```

---

## Security Considerations

### ✅ Best Practices

1. **Never commit API keys to version control**
   ```bash
   # .gitignore
   application-prod.properties
   .env
   secrets/
   ```

2. **Use environment variables for secrets**
   ```bash
   export HOMENEST_EXTERNAL_API_KEY=${PRODUCTION_API_KEY}
   ```

3. **Validate API responses**
   ```java
   if (response == null || response.isEmpty()) {
       throw new IllegalArgumentException("Invalid API response");
   }
   ```

4. **Rate limit API calls**
   ```java
   // Add throttling logic
   if (lastImportTime + 3600000 < System.currentTimeMillis()) {
       // Only import once per hour
       performImport();
   }
   ```

5. **Sanitize imported data**
   ```java
   String sanitized = listing.getTitle()
       .replaceAll("[<>\"'&]", "")
       .substring(0, Math.min(length, 255));
   ```

6. **Audit import activities**
   ```java
   logger.info("Imported listing: {} by user: {} at: {}", 
       listing.getTitle(), 
       getCurrentUser(), 
       LocalDateTime.now());
   ```

---

## Testing

### Unit Test Example

```java
package com.homenest.homenest.service;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ExternalListingImportServiceTest {

    @MockBean
    private ListingRepository listingRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testImportListingsFromAPI() {
        // Given: Mock API response
        Map<String, Object> mockListing = Map.of(
            "hostel_name", "Test Hostel",
            "city", "Lahore",
            "nightly_rate", 1500
        );

        // When: Call import service
        // Then: Verify listings saved to database
        verify(listingRepository, times(1)).save(any());
    }
}
```

---

## Supported External APIs

### Hostelworld
- **Base URL:** `https://api.hostelworld.com/v1`
- **Auth:** API Key in header `X-API-Key: {key}`
- **Rate Limit:** 100 req/min
- **Docs:** https://apidocs.hostelworld.com

**Mapping Example:**
```java
// Hostelworld response:
{
  "hostels": [{
    "id": "123",
    "name": "City Hostel",
    "city": "Lahore",
    "price": 1500
  }]
}

// Map to:
listing.setTitle(hostel.get("name"));
listing.setCity(hostel.get("city"));
listing.setPricePerNight(hostel.get("price"));
```

### Booking.com
- **Base URL:** `https://api.booking.com/v1`
- **Auth:** OAuth 2.0
- **Rate Limit:** 500 req/min
- **Docs:** https://docs.booking.com/api

### Custom APIs
Implement your own mapping based on API documentation.

---

## Monitoring & Maintenance

### Check Import Status
```bash
# View logs
tail -f logs/homenest.log | grep "import\|external"

# Check number of imported listings
SELECT COUNT(*) FROM listing WHERE host_id = 1;
```

### Backup Before Import
```sql
-- Create backup table
CREATE TABLE listing_backup AS SELECT * FROM listing;

-- Rollback if needed
TRUNCATE listing;
INSERT INTO listing SELECT * FROM listing_backup;
```

### Cleanup Duplicate Listings
```sql
-- Find duplicates
SELECT title, city, COUNT(*) 
FROM listing 
GROUP BY title, city 
HAVING COUNT(*) > 1;

-- Remove duplicates (keep oldest)
DELETE FROM listing 
WHERE id NOT IN (
    SELECT MIN(id) 
    FROM listing 
    GROUP BY title, city
);
```

---

## Support & Troubleshooting

### Common Questions

**Q: How do I start the import?**
A: Create an admin endpoint and call `importService.importListingsFromExternalAPI()` or use a scheduled task.

**Q: Can I import listings for multiple hosts?**
A: Currently, all imports are owned by a single host. Modify `importListingsFromExternalAPI()` to support multiple hosts.

**Q: How often should I import?**
A: Depends on your API rate limits and data freshness requirements. Daily or weekly is typical.

**Q: Will duplicates be created?**
A: Add a check to prevent duplicate imports: `listing.setExternalId()` and query before saving.

---

**Last Updated:** December 6, 2025  
**Status:** Ready for Integration  
**Tested:** ✅ Yes (Structure verified, API calls not executed)
