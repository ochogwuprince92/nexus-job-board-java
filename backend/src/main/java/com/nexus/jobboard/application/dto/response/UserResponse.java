package com.nexus.jobboard.application.dto.response;

import com.nexus.jobboard.domain.model.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User response DTO following SRP
 * - Single responsibility: Present user data to clients
 * - Excludes sensitive information like passwords
 */
@Data
@Builder
public class UserResponse {
    
    private Long id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    public String getRoleDescription() {
        return role != null ? role.getDescription() : null;
    }
    
    public boolean canManageJobs() {
        return role == UserRole.ADMIN || role == UserRole.EMPLOYER;
    }
}
