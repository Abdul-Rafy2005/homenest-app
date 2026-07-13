package com.homenest.homenest;

import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HomenestApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomenestApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setEmail("admin@homenest.com");
				admin.setPassword("1234");
				admin.setRole("ROLE_ADMIN");
				userRepository.save(admin);
				System.out.println("Admin user created: admin / 1234");
			}
		};
	}
}
