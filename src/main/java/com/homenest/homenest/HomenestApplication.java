package com.homenest.homenest;

import com.homenest.homenest.model.Listing;
import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.ListingRepository;
import com.homenest.homenest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@SpringBootApplication
public class HomenestApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomenestApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserRepository userRepository, ListingRepository listingRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// Create users if they don't exist
			if (userRepository.count() == 0) {
				System.out.println("Initializing users...");

				User host1 = new User();
				host1.setUsername("host1");
				host1.setEmail("host1@homenest.com");
				host1.setPassword("1234");
				host1.setRole("ROLE_HOST");
				userRepository.save(host1);

				User guest1 = new User();
				guest1.setUsername("guest1");
				guest1.setEmail("guest1@homenest.com");
				guest1.setPassword("1234");
				guest1.setRole("ROLE_GUEST");
				userRepository.save(guest1);

				User admin = new User();
				admin.setUsername("admin");
				admin.setEmail("admin@homenest.com");
				admin.setPassword("1234");
				admin.setRole("ROLE_ADMIN");
				userRepository.save(admin);

				System.out.println("Created users: host1, guest1, admin");
			}

			// Create realistic Pakistan hostel seed data if listings don't exist
			if (listingRepository.count() == 0) {
				System.out.println("Initializing realistic Pakistan hostel listings...");

				User host = userRepository.findByUsername("host1").orElseThrow();

				// KARACHI (5 listings)
				createListing(listingRepository, host, "Backpackers Hub - Saddar, Karachi",
						"Budget-friendly hostel in the heart of Saddar with shared dormitories, free WiFi, and access to nearby restaurants and shops.",
						"Saddar", "45 Main Boulevard, Saddar", "HOSTEL", 8, 1500);
				createListing(listingRepository, host, "Sea View Budget Stay",
						"Affordable accommodation near Sea View with ocean breeze views. Common kitchen, lounge area, and 24-hour security.",
						"Karachi", "Sea View Road", "HOSTEL", 12, 1800);
				createListing(listingRepository, host, "Downtown Traveler's Lodge",
						"Central location with clean beds, hot showers, and friendly staff. Perfect base for exploring Karachi's markets and attractions.",
						"Karachi", "Zaibunnisa Street", "HOSTEL", 6, 1200);
				createListing(listingRepository, host, "Clifton Budget Rest",
						"Cozy rooms near Clifton Beach. Includes breakfast, WiFi, and arrangement of local tours and beach activities.",
						"Clifton", "Clifton Block 5", "HOSTEL", 4, 2200);
				createListing(listingRepository, host, "Guest Quarters Karachi",
						"Centrally located with basic but clean rooms. Good for short stays with friendly atmosphere and helpful owner.",
						"Karachi", "Kala Board", "HOSTEL", 10, 1400);

				// LAHORE (5 listings)
				createListing(listingRepository, host, "Mall Road Backpackers Inn",
						"Historic hostel near Mall Road with rooftop seating area overlooking the city. Great for meeting fellow travelers.",
						"Lahore", "42 Mall Road", "HOSTEL", 16, 1600);
				createListing(listingRepository, host, "Old City Traveler's Rest",
						"Located in the heart of the old walled city near Badshahi Mosque. Experience Lahore's rich heritage and local culture.",
						"Lahore", "Walled City Near Gate", "HOSTEL", 8, 1400);
				createListing(listingRepository, host, "DHA Youth Hostel",
						"Modern hostel in Defence Housing Authority with good amenities, common kitchen, and comfortable dorms.",
						"Lahore", "DHA Phase 2", "HOSTEL", 10, 1900);
				createListing(listingRepository, host, "Canal Road Budget Stay",
						"Budget accommodation with clean facilities near Canal Road. Easy access to shops, restaurants, and public transport.",
						"Lahore", "Canal Road", "HOSTEL", 12, 1300);
				createListing(listingRepository, host, "Naulakha Haveli Experience",
						"Stay in a historic haveli converted into a guesthouse with traditional décor. Includes guided tours and local experiences.",
						"Lahore", "Walled City", "HOSTEL", 6, 2100);

				// ISLAMABAD (5 listings)
				createListing(listingRepository, host, "Margalla Trekkers Lodge",
						"Perfect base for exploring Margalla Hills with hiking guides available. Comfortable rooms and mountain views.",
						"Islamabad", "F-8 Sector, Near Margalla", "HOSTEL", 10, 1700);
				createListing(listingRepository, host, "Downtown Islamabad Hostel",
						"Central location in Blue Area with easy access to markets, restaurants, and Faisal Mosque.",
						"Islamabad", "Blue Area, F-10", "HOSTEL", 8, 1500);
				createListing(listingRepository, host, "Family Rooms Islamabad",
						"Mix of dorms and private family rooms near Haniif Center. Includes free breakfast and WiFi.",
						"Islamabad", "Haniif Center, G-6", "HOSTEL", 6, 1800);
				createListing(listingRepository, host, "Pir Sohawa Mountain Stay",
						"Elevated location with panoramic city views. Great for nature lovers and photographers.",
						"Islamabad", "Pir Sohawa", "HOSTEL", 4, 2000);
				createListing(listingRepository, host, "Jinnah Super Market Budget Lodge",
						"Budget-friendly option near Jinnah Super with convenient location for shopping and dining.",
						"Islamabad", "Jinnah Super Market, F-7", "HOSTEL", 12, 1400);

				// PESHAWAR (5 listings)
				createListing(listingRepository, host, "Qissa Khwani Bazaar Inn",
						"Traditional guesthouse in historic bazaar with cultural atmosphere. Experience old Peshawar while staying comfortably.",
						"Peshawar", "Qissa Khwani Bazaar", "HOSTEL", 6, 1100);
				createListing(listingRepository, host, "Cantonment Budget Rest",
						"Located in safe cantonment area with modern facilities and friendly staff.",
						"Peshawar", "Cantonment Road", "HOSTEL", 10, 1300);
				createListing(listingRepository, host, "Peshawar City Center Hostel",
						"Close to main city attractions with good public transport access. Clean rooms and helpful management.",
						"Peshawar", "Saddar Bazaar", "HOSTEL", 8, 1200);
				createListing(listingRepository, host, "University Town Guest House",
						"Peaceful location near University of Peshawar with quiet environment for students and researchers.",
						"Peshawar", "University Town", "HOSTEL", 12, 1000);
				createListing(listingRepository, host, "Khyber Bazaar Lodge",
						"Heritage accommodation in traditional setting with Pashto hospitality. Includes traditional meals available.",
						"Peshawar", "Khyber Bazaar", "HOSTEL", 5, 1050);

				// HYDERABAD (5 listings)
				createListing(listingRepository, host, "Sindhi Cultural Hostel",
						"Learn about Sindhi culture in this heritage-focused accommodation. Hosts local events and cultural programs.",
						"Hyderabad", "Sindh Street", "HOSTEL", 8, 1250);
				createListing(listingRepository, host, "Clock Tower Budget Stay",
						"Near historic Clock Tower with access to old markets and bazaars. Great for culture enthusiasts.",
						"Hyderabad", "Clock Tower Area", "HOSTEL", 10, 1150);
				createListing(listingRepository, host, "Tourist Hostel Hyderabad",
						"Professional hostel with tour arrangement services. Perfect for exploring Sindh region.",
						"Hyderabad", "G.M. Abad", "HOSTEL", 12, 1300);
				createListing(listingRepository, host, "Lakeside Rest Hyderabad",
						"Budget accommodation near Indus River with peaceful environment. Good for photography and relaxation.",
						"Hyderabad", "Nizam Abad", "HOSTEL", 6, 1400);
				createListing(listingRepository, host, "University Enclave Hostel",
						"Located near Hamdard University with academic atmosphere and quiet surroundings.",
						"Hyderabad", "University Road", "HOSTEL", 14, 1100);

				// QUETTA (5 listings)
				createListing(listingRepository, host, "Mountain View Hostel Quetta",
						"Elevated location with beautiful mountain views. Cool climate perfect for summer travelers.",
						"Quetta", "Chiltan Road", "HOSTEL", 6, 1350);
				createListing(listingRepository, host, "Baluchistan Travelers Inn",
						"Traditional Balochi hospitality with modern amenities. Access to local guides for Baluchistan tours.",
						"Quetta", "Zarghoon Road", "HOSTEL", 8, 1200);
				createListing(listingRepository, host, "City Center Budget Lodge",
						"Central location in heart of Quetta with easy access to shops and restaurants.",
						"Quetta", "Jinnah Road", "HOSTEL", 10, 1150);
				createListing(listingRepository, host, "Desert Explorer Hostel",
						"Base camp for desert and mountain adventures. Organizes trips to Hanna Lake and surrounding areas.",
						"Quetta", "Hanna Road", "HOSTEL", 4, 1500);
				createListing(listingRepository, host, "Fort Road Rest House",
						"Budget accommodation with courtyard and traditional design. Safe and secure location.",
						"Quetta", "Fort Road", "HOSTEL", 12, 1100);

				// FAISALABAD (5 listings)
				createListing(listingRepository, host, "Textile City Hostel",
						"Budget hostel in Faisalabad's business district. Convenient for business travelers and students.",
						"Faisalabad", "Millat Road", "HOSTEL", 10, 1250);
				createListing(listingRepository, host, "Garden City Backpackers",
						"Located near botanical gardens with green surroundings. Great for nature-loving travelers.",
						"Faisalabad", "Jilani Road", "HOSTEL", 8, 1300);
				createListing(listingRepository, host, "Clock Tower Lodge",
						"Near iconic Clock Tower in city center with central location for all attractions.",
						"Faisalabad", "Clock Tower", "HOSTEL", 12, 1200);
				createListing(listingRepository, host, "University Hostel Faisalabad",
						"Budget accommodation near University of Agriculture. Popular with academic researchers.",
						"Faisalabad", "University Road", "HOSTEL", 6, 1000);
				createListing(listingRepository, host, "Sunrise Guest House",
						"Well-maintained hostel with rooftop area and friendly management. Good breakfast options.",
						"Faisalabad", "Civil Lines", "HOSTEL", 10, 1350);

				// MULTAN (5 listings)
				createListing(listingRepository, host, "Sufi Heritage Hostel",
						"Experience Multan's rich Sufi heritage in this culturally-focused accommodation near historic shrines.",
						"Multan", "Shah Rukn-e-Alam Road", "HOSTEL", 8, 1200);
				createListing(listingRepository, host, "Chinar Garden Traveler's Rest",
						"Beautiful garden setting with peaceful atmosphere. Perfect for spiritual travelers.",
						"Multan", "Khanewal Road", "HOSTEL", 6, 1350);
				createListing(listingRepository, host, "Fort Area Budget Stay",
						"Located near Multan Fort with easy access to historical sites and bazaars.",
						"Multan", "Sheesh Mahal Road", "HOSTEL", 10, 1150);
				createListing(listingRepository, host, "Cantonment Hostel Multan",
						"Safe, secure location with good amenities and friendly staff. Popular with families.",
						"Multan", "Cantonment Road", "HOSTEL", 12, 1300);
				createListing(listingRepository, host, "Sufi City Lodge",
						"Budget accommodation for pilgrims and travelers exploring Multan's spiritual sites.",
						"Multan", "Old City", "HOSTEL", 5, 950);

				System.out.println("Created 40 realistic Pakistan hostel listings across 8 cities");
			}

			System.out.println("Data initialization complete!");
		};
	}

	private void createListing(ListingRepository listingRepository, User host, String title, String description,
			String city, String address, String propertyType, int capacity, int pricePerNight) {
		Listing listing = new Listing();
		listing.setHost(host);
		listing.setTitle(title);
		listing.setDescription(description);
		listing.setCity(city);
		listing.setAddress(address);
		listing.setPropertyType(propertyType);
		listing.setCapacity(capacity);
		listing.setPricePerNight(new BigDecimal(pricePerNight));
		listing.setIsPublished(true);
		listingRepository.save(listing);
	}
}
