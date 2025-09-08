package com.drivelah.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthUser {
    
    private String userId;
    private String email;
    private String name;
    private String role; // HOST, RENTER, ADMIN
    private String phoneNumber;
    private boolean isVerified;
    
    public AuthUser() {}
    
    public AuthUser(String userId, String email, String name, String role, String phoneNumber, boolean isVerified) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.isVerified = isVerified;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @JsonProperty("isVerified")
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    // Utility methods
    public boolean isHost() {
        return "HOST".equals(role);
    }
    
    public boolean isRenter() {
        return "RENTER".equals(role);
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}