package com.example.expense_tracker.config;

import com.example.expense_tracker.service.JwtService;
import com.example.expense_tracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        // Check header exists
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7); // remove "Bearer "
            username = jwtService.extractUsername(token);
        }

        // If username found and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            userRepository.findByUsername(username).ifPresent(user -> {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                Collections.emptyList()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}