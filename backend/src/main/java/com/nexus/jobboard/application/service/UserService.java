package com.nexus.jobboard.application.service;

import com.nexus.jobboard.application.dto.request.UserRegistrationRequest;
import com.nexus.jobboard.application.dto.request.UserUpdateRequest;
import com.nexus.jobboard.application.dto.response.UserResponse;
import com.nexus.jobboard.domain.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * User service interface following DIP (Dependency Inversion Principle)
 * - High-level module that depends on abstractions
 * - Defines contract for user-related business operations
 */
public interface UserService {
    
    /**
     * Register a new user in the system
     */
    UserResponse registerUser(UserRegistrationRequest request);
    
    /**
     * Update existing user information
     */
    UserResponse updateUser(Long userId, UserUpdateRequest request);
    
    /**
     * Get user by ID
     */
    Optional<UserResponse> getUserById(Long userId);
    
    /**
     * Get user by email or phone number
     */
    Optional<UserResponse> getUserByEmailOrPhone(String identifier);
    
    /**
     * Get all users with pagination
     */
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    /**
     * Get users by role with pagination
     */
    Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable);
    
    /**
     * Search users by name or email
     */
    Page<UserResponse> searchUsers(String searchTerm, Pageable pageable);
    
    /**
     * Deactivate user account
     */
    void deactivateUser(Long userId);
    
    /**
     * Activate user account
     */
    void activateUser(Long userId);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Verify user's email address
     */
    void verifyEmail(Long userId);
    
    /**
     * Verify user's phone number
     */
    void verifyPhoneNumber(Long userId);
    
    /**
     * Get user statistics by role
     */
    Long getUserCountByRole(UserRole role);
}
