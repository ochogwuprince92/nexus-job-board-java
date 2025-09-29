package com.nexus.jobboard.presentation.controller;

import com.nexus.jobboard.application.dto.request.LoginRequest;
import com.nexus.jobboard.application.dto.request.RefreshTokenRequest;
import com.nexus.jobboard.application.dto.request.UserRegistrationRequest;
import com.nexus.jobboard.application.dto.response.AuthenticationResponse;
import com.nexus.jobboard.application.dto.response.UserResponse;
import com.nexus.jobboard.application.service.AuthenticationService;
import com.nexus.jobboard.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller following SRP
 * - Single responsibility: Handle authentication and registration endpoints
 * - Depends on service abstractions (DIP)
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email or phone number")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request received for role: {}", request.getRole());
        
        if (!request.isValid()) {
            return ResponseEntity.badRequest().build();
        }
        
        UserResponse response = userService.registerUser(request);
        log.info("User registered successfully with ID: {}", response.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email/phone and password")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for username: {}", request.getUsername());
        
        AuthenticationResponse response = authenticationService.login(request);
        log.info("User logged in successfully: {}", request.getUsername());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");
        
        AuthenticationResponse response = authenticationService.refreshToken(request);
        log.info("Token refreshed successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidate user session and tokens")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request received");
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            authenticationService.logout(jwtToken);
            log.info("User logged out successfully");
        }
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Check if the provided token is valid")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        
        String jwtToken = token.substring(7);
        boolean isValid = authenticationService.validateToken(jwtToken);
        
        return ResponseEntity.ok(isValid);
    }
}
