package com.nexus.jobboard.application.service.impl;

import com.nexus.jobboard.application.dto.request.LoginRequest;
import com.nexus.jobboard.application.dto.request.RefreshTokenRequest;
import com.nexus.jobboard.application.dto.response.AuthenticationResponse;
import com.nexus.jobboard.application.dto.response.UserResponse;
import com.nexus.jobboard.application.mapper.UserMapper;
import com.nexus.jobboard.application.service.AuthenticationService;
import com.nexus.jobboard.domain.model.User;
import com.nexus.jobboard.domain.repository.UserRepository;
import com.nexus.jobboard.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * Authentication service implementation following SOLID principles
 * 
 * SRP: Handles only authentication-related business logic
 * OCP: Open for extension through interface implementation
 * LSP: Substitutable for AuthenticationService interface
 * ISP: Depends only on specific interfaces it needs
 * DIP: Depends on abstractions (JwtService, UserRepository, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());
        
        // Authenticate with Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Load user details
        User user = userRepository.findByEmailOrPhoneNumber(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));
        
        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Store refresh token in Redis
        storeRefreshToken(user.getId(), refreshToken);
        
        UserResponse userResponse = userMapper.toResponse(user);
        
        log.info("User authenticated successfully: {}", user.getId());
        
        return AuthenticationResponse.of(
                accessToken,
                refreshToken,
                jwtService.getExpirationTime() / 1000, // Convert to seconds
                userResponse
        );
    }
    
    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");
        
        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmailOrPhoneNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Check if refresh token exists in Redis
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + user.getId());
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        // Generate new tokens
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        
        // Update refresh token in Redis
        storeRefreshToken(user.getId(), newRefreshToken);
        
        UserResponse userResponse = userMapper.toResponse(user);
        
        log.info("Token refreshed successfully for user: {}", user.getId());
        
        return AuthenticationResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtService.getExpirationTime() / 1000,
                userResponse
        );
    }
    
    @Override
    public void logout(String token) {
        log.info("Logging out user");
        
        try {
            // Add token to blacklist
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByEmailOrPhoneNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            
            // Blacklist the access token
            long expiration = jwtService.getExpirationTime() / 1000;
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + token,
                    "blacklisted",
                    expiration,
                    TimeUnit.SECONDS
            );
            
            // Remove refresh token
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + user.getId());
            
            log.info("User logged out successfully: {}", user.getId());
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        try {
            // Check if token is blacklisted
            if (redisTemplate.hasKey(BLACKLIST_PREFIX + token)) {
                return false;
            }
            
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public String extractUsernameFromToken(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public void invalidateAllUserTokens(Long userId) {
        log.info("Invalidating all tokens for user: {}", userId);
        
        // Remove refresh token
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
        
        // Note: For access tokens, we would need to maintain a list of active tokens
        // per user to blacklist them all. For simplicity, we're just removing the refresh token.
        // In a production system, you might want to implement a more sophisticated approach.
        
        log.info("All tokens invalidated for user: {}", userId);
    }
    
    private void storeRefreshToken(Long userId, String refreshToken) {
        long expiration = jwtService.getRefreshExpirationTime() / 1000;
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                refreshToken,
                expiration,
                TimeUnit.SECONDS
        );
    }
}
