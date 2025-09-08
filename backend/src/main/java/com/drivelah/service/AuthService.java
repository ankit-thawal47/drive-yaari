package com.drivelah.service;

import com.drivelah.model.auth.AuthRequest;
import com.drivelah.model.auth.AuthResponse;
import com.drivelah.model.auth.AuthUser;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    // Mock user database - in production this would be a real database
    // Hardcoding it for MVP
    private final Map<String, AuthUser> mockUsers = new HashMap<>();
    private final Map<String, String> emailToPassword = new HashMap<>();

    public AuthService() {
        // Initialize with predefined test-mock users
        initializeMockUsers();
    }

    public AuthResponse login(AuthRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getPassword() == null) {
                return AuthResponse.failure("Email and password are required");
            }

            String storedPassword = emailToPassword.get(request.getEmail().toLowerCase());
            if (storedPassword == null) {
                return AuthResponse.failure("User not found");
            }

            if (!storedPassword.equals(request.getPassword())) {
                return AuthResponse.failure("Invalid password");
            }

            AuthUser user = findUserByEmail(request.getEmail());
            if (user == null) {
                return AuthResponse.failure("User not found");
            }

            String token = generateMockToken(user);

            return AuthResponse.success(token, user);

        } catch (Exception e) {
            return AuthResponse.failure("Login failed: " + e.getMessage());
        }
    }

    public AuthResponse register(AuthRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getPassword() == null ||
                    request.getName() == null || request.getRole() == null) {
                return AuthResponse.failure("Email, password, name, and role are required");
            }

            // Check if user already exists
            if (emailToPassword.containsKey(request.getEmail().toLowerCase())) {
                return AuthResponse.failure("User already exists with this email");
            }

            // Validate role
            if (!isValidRole(request.getRole())) {
                return AuthResponse.failure("Role must be HOST or RENTER");
            }

            // Create new user
            String userId = "USER-" + UUID.randomUUID().toString().substring(0, 8);
            AuthUser newUser = new AuthUser(
                    userId,
                    request.getEmail().toLowerCase(),
                    request.getName(),
                    request.getRole().toUpperCase(),
                    request.getPhoneNumber(),
                    false // New users start unverified
            );

            // Store user
            mockUsers.put(userId, newUser);
            emailToPassword.put(request.getEmail().toLowerCase(), request.getPassword());

            // Generate token
            String token = generateMockToken(newUser);

            return AuthResponse.success(token, newUser);

        } catch (Exception e) {
            return AuthResponse.failure("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Validate mock token and return user
     */
    public AuthUser validateToken(String token) {
        try {
            if (token == null || !token.startsWith("MOCK-TOKEN-")) {
                return null;
            }

            // Extract user ID from mock token
            String encodedData = token.substring("MOCK-TOKEN-".length());
            String decodedData = new String(Base64.getDecoder().decode(encodedData));

            // Format: "userId:email:role:timestamp"
            String[] parts = decodedData.split(":");
            if (parts.length < 3) {
                return null;
            }

            String userId = parts[0];
            return mockUsers.get(userId);

        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get user by ID
     */
    public AuthUser getUserById(String userId) {
        return mockUsers.get(userId);
    }

    public Map<String, AuthUser> getAllUsers() {
        return new HashMap<>(mockUsers);
    }

    // Private helper methods

    private void initializeMockUsers() {
        // Create test users for each role

        // Test Host
        AuthUser host = new AuthUser(
                "HOST-001",
                "host@drivelah.com",
                "John Host",
                "HOST",
                "+91-9876-5432",
                true
        );
        mockUsers.put("HOST-001", host);
        emailToPassword.put("host@drivelah.com", "password123");

        // Test Renter  
        AuthUser renter = new AuthUser(
                "RENTER-001",
                "renter@drivelah.com",
                "Jane Renter",
                "RENTER",
                "+91-1234-5678",
                true
        );
        mockUsers.put("RENTER-001", renter);
        emailToPassword.put("renter@drivelah.com", "password123");

        // Test Admin
        AuthUser admin = new AuthUser(
                "ADMIN-001",
                "admin@drivelah.com",
                "Admin User",
                "ADMIN",
                "+91-9999-0000",
                true
        );
        mockUsers.put("ADMIN-001", admin);
        emailToPassword.put("admin@drivelah.com", "admin123");

        // Additional test users
        AuthUser unverifiedHost = new AuthUser(
                "HOST-002",
                "newhost@drivelah.com",
                "New Host",
                "HOST",
                "+91-8888-7777",
                false
        );
        mockUsers.put("HOST-002", unverifiedHost);
        emailToPassword.put("newhost@drivelah.com", "password123");
    }

    private AuthUser findUserByEmail(String email) {
        return mockUsers.values().stream()
                .filter(user -> user.getEmail().equals(email.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private String generateMockToken(AuthUser user) {
        String tokenData = user.getUserId() + ":" + user.getEmail() + ":" +
                user.getRole() + ":" + System.currentTimeMillis();
        String encodedData = Base64.getEncoder().encodeToString(tokenData.getBytes());
        return "MOCK-TOKEN-" + encodedData;
    }

    private boolean isValidRole(String role) {
        return "HOST".equalsIgnoreCase(role) || "RENTER".equalsIgnoreCase(role);
    }
}