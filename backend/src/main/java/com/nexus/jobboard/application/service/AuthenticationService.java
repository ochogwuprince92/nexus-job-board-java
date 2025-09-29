package com.nexus.jobboard.application.service;

import com.nexus.jobboard.application.dto.request.LoginRequest;
import com.nexus.jobboard.application.dto.request.RefreshTokenRequest;
import com.nexus.jobboard.application.dto.response.AuthenticationResponse;

/**
 * Authentication service interface following DIP and SRP
 * - Depends on abstractions, not concrete implementations
 * - Single responsibility: Authentication and token management
 */
public interface AuthenticationService {
    
    /**
     * Authenticate user with credentials
     */
    AuthenticationResponse login(LoginRequest request);
    
    /**
     * Refresh access token using refresh token
     */
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    
    /**
     * Logout user and invalidate tokens
     */
    void logout(String token);
    
    /**
     * Validate JWT token
     */
    boolean validateToken(String token);
    
    /**
     * Extract username from JWT token
     */
    String extractUsernameFromToken(String token);
    
    /**
     * Invalidate all tokens for a user
     */
    void invalidateAllUserTokens(Long userId);
}
