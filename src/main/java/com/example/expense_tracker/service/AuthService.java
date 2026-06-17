package com.example.expense_tracker.service;

import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    // Register
    public User register(User user) {
        return userRepository.save(user);
    }

    // Login
    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(username);
    }
}