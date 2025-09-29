package com.nexus.jobboard.domain.repository;

import com.nexus.jobboard.domain.model.User;
import com.nexus.jobboard.domain.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User repository interface following ISP (Interface Segregation Principle)
 * - Contains only user-specific operations
 * - Segregated from other domain repositories
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Authentication queries
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phoneNumber = :identifier")
    Optional<User> findByEmailOrPhoneNumber(@Param("identifier") String identifier);
    
    // Existence checks
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Role-based queries
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    Page<User> findByRoleAndIsActive(UserRole role, Boolean isActive, Pageable pageable);
    
    // Status queries
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    // Search functionality
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "u.isActive = true")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    Long countActiveUsersByRole(@Param("role") UserRole role);
}
