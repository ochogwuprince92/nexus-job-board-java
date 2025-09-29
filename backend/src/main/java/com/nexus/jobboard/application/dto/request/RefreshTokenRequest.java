package com.nexus.jobboard.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Refresh token request DTO following SRP
 * - Single responsibility: Handle refresh token validation
 */
@Data
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
