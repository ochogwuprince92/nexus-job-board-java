package com.nexus.jobboard.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Authentication response DTO following SRP
 * - Single responsibility: Present authentication data to clients
 */
@Data
@Builder
public class AuthenticationResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn; // in seconds
    private LocalDateTime expiresAt;
    private UserResponse user;
    
    public static AuthenticationResponse of(String accessToken, String refreshToken, 
                                          Long expiresIn, UserResponse user) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .expiresAt(LocalDateTime.now().plusSeconds(expiresIn))
                .user(user)
                .build();
    }
}
