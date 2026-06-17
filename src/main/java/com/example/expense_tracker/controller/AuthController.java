package com.example.expense_tracker.controller;

import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register API
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    // Login API
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {

        String token = authService.login(user.getUsername(), user.getPassword());

        return Map.of("token", token);
    }
}