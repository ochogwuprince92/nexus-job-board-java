package com.nexus.jobboard.application.service.impl;

import com.nexus.jobboard.application.dto.request.UserRegistrationRequest;
import com.nexus.jobboard.application.dto.request.UserUpdateRequest;
import com.nexus.jobboard.application.dto.response.UserResponse;
import com.nexus.jobboard.application.mapper.UserMapper;
import com.nexus.jobboard.application.service.UserService;
import com.nexus.jobboard.domain.model.User;
import com.nexus.jobboard.domain.model.UserRole;
import com.nexus.jobboard.domain.repository.UserRepository;
import com.nexus.jobboard.infrastructure.exception.DuplicateResourceException;
import com.nexus.jobboard.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User service implementation following SOLID principles
 * 
 * SRP: Handles only user-related business logic
 * OCP: Open for extension through interface implementation
 * LSP: Substitutable for UserService interface
 * ISP: Depends only on specific interfaces it needs
 * DIP: Depends on abstractions (UserRepository, PasswordEncoder, UserMapper)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    
    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with role: {} and identifier: {}", 
                request.getRole(), request.getPrimaryIdentifier());
        
        validateRegistrationRequest(request);
        
        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return userMapper.toResponse(savedUser);
    }
    
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", userId);
        
        if (!request.hasUpdates()) {
            throw new IllegalArgumentException("No updates provided");
        }
        
        User user = findUserById(userId);
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            validateEmailNotExists(request.getEmail());
            user.setEmail(request.getEmail());
            user.setIsEmailVerified(false);
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            validatePhoneNotExists(request.getPhoneNumber());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setIsPhoneVerified(false);
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#userId")
    public Optional<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#identifier")
    public Optional<UserResponse> getUserByEmailOrPhone(String identifier) {
        return userRepository.findByEmailOrPhoneNumber(identifier)
                .map(userMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRoleAndIsActive(role, true, pageable)
                .map(userMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findBySearchTerm(searchTerm, pageable)
                .map(userMapper::toResponse);
    }
    
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);
        User user = findUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully: {}", userId);
    }
    
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void activateUser(Long userId) {
        log.info("Activating user with ID: {}", userId);
        User user = findUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated successfully: {}", userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void verifyEmail(Long userId) {
        log.info("Verifying email for user ID: {}", userId);
        User user = findUserById(userId);
        user.setIsEmailVerified(true);
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", userId);
    }
    
    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void verifyPhoneNumber(Long userId) {
        log.info("Verifying phone number for user ID: {}", userId);
        User user = findUserById(userId);
        user.setIsPhoneVerified(true);
        userRepository.save(user);
        log.info("Phone number verified successfully for user: {}", userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userStats", key = "#role")
    public Long getUserCountByRole(UserRole role) {
        return userRepository.countActiveUsersByRole(role);
    }
    
    // Private helper methods following SRP
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
    
    private void validateRegistrationRequest(UserRegistrationRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Either email or phone number must be provided");
        }
        
        if (request.getEmail() != null) {
            validateEmailNotExists(request.getEmail());
        }
        
        if (request.getPhoneNumber() != null) {
            validatePhoneNotExists(request.getPhoneNumber());
        }
    }
    
    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }
    }
    
    private void validatePhoneNotExists(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateResourceException("User", "phone number", phoneNumber);
        }
    }
}
