package com.nexus.jobboard.presentation.controller;

import com.nexus.jobboard.application.dto.request.UserUpdateRequest;
import com.nexus.jobboard.application.dto.response.UserResponse;
import com.nexus.jobboard.application.service.UserService;
import com.nexus.jobboard.domain.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * User controller following SRP
 * - Single responsibility: Handle user management endpoints
 * - Depends on service abstractions (DIP)
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        log.info("Getting current user profile for: {}", authentication.getName());
        
        return userService.getUserByEmailOrPhone(authentication.getName())
                .map(user -> {
                    log.info("Current user profile retrieved: {}", user.getId());
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Update the authenticated user's profile information")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        log.info("Updating current user profile for: {}", authentication.getName());
        
        return userService.getUserByEmailOrPhone(authentication.getName())
                .map(user -> {
                    UserResponse updated = userService.updateUser(user.getId(), request);
                    log.info("User profile updated successfully: {}", user.getId());
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by ID (Admin only or own profile)")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#userId).orElse(null)?.id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.info("Getting user by ID: {}", userId);
        
        return userService.getUserById(userId)
                .map(user -> {
                    log.info("User retrieved successfully: {}", userId);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.info("Getting all users with pagination");
        
        Page<UserResponse> users = userService.getAllUsers(pageable);
        log.info("Retrieved {} users", users.getTotalElements());
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or email (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        log.info("Searching users with query: {}", query);
        
        Page<UserResponse> users = userService.searchUsers(query, pageable);
        log.info("Found {} users matching query: {}", users.getTotalElements(), query);
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieve users by role with pagination (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(
            @PathVariable UserRole role,
            Pageable pageable) {
        log.info("Getting users by role: {}", role);
        
        Page<UserResponse> users = userService.getUsersByRole(role, pageable);
        log.info("Retrieved {} users with role: {}", users.getTotalElements(), role);
        
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        log.info("Deactivating user: {}", userId);
        
        userService.deactivateUser(userId);
        log.info("User deactivated successfully: {}", userId);
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{userId}/activate")
    @Operation(summary = "Activate user", description = "Activate user account (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        log.info("Activating user: {}", userId);
        
        userService.activateUser(userId);
        log.info("User activated successfully: {}", userId);
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{userId}/verify-email")
    @Operation(summary = "Verify user email", description = "Mark user email as verified (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyEmail(@PathVariable Long userId) {
        log.info("Verifying email for user: {}", userId);
        
        userService.verifyEmail(userId);
        log.info("Email verified successfully for user: {}", userId);
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{userId}/verify-phone")
    @Operation(summary = "Verify user phone", description = "Mark user phone as verified (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyPhone(@PathVariable Long userId) {
        log.info("Verifying phone for user: {}", userId);
        
        userService.verifyPhoneNumber(userId);
        log.info("Phone verified successfully for user: {}", userId);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats/role/{role}")
    @Operation(summary = "Get user count by role", description = "Get the count of active users by role (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getUserCountByRole(@PathVariable UserRole role) {
        log.info("Getting user count for role: {}", role);
        
        Long count = userService.getUserCountByRole(role);
        log.info("User count for role {}: {}", role, count);
        
        return ResponseEntity.ok(count);
    }
}
