package com.drivelah.model.auth;

/**
 * Authentication response model
 */
public class AuthResponse {
    
    private boolean success;
    private String message;
    private String token;           // Mock JWT token
    private AuthUser user;      // User information
    private long expiresIn;         // Token expiry in seconds
    
    public AuthResponse() {}
    
    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public AuthResponse(boolean success, String message, String token, AuthUser user, long expiresIn) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
    }
    
    // Static factory methods for common responses
    public static AuthResponse success(String token, AuthUser user) {
        return new AuthResponse(true, "Authentication successful", token, user, 86400); // 24 hours
    }
    
    public static AuthResponse failure(String message) {
        return new AuthResponse(false, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public AuthUser getUser() {
        return user;
    }
    
    public void setUser(AuthUser user) {
        this.user = user;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}