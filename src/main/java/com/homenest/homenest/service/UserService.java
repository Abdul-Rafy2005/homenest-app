package com.homenest.homenest.service;

import com.homenest.homenest.model.User;
import com.homenest.homenest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String username, String email, String rawPassword, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(rawPassword); // Store plain password
        user.setRole(role);

        return userRepository.save(user);
    }

    public User registerGuest(String username, String email, String rawPassword) {
        return registerUser(username, email, rawPassword, "ROLE_GUEST");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findAllHosts() {
        return userRepository.findByRole("ROLE_HOST");
    }

    public List<User> findAllGuests() {
        return userRepository.findByRole("ROLE_GUEST");
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
