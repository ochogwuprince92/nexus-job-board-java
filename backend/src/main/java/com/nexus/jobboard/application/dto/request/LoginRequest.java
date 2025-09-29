package com.nexus.jobboard.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login request DTO following SRP
 * - Single responsibility: Handle login credentials validation
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "Username (email or phone) is required")
    private String username; // Can be email or phone number
    
    @NotBlank(message = "Password is required")
    private String password;
}
