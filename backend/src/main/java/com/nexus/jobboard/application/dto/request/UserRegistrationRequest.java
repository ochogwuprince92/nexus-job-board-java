package com.nexus.jobboard.application.dto.request;

import com.nexus.jobboard.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * User registration request DTO following SRP
 * - Single responsibility: Handle user registration data validation
 */
@Data
public class UserRegistrationRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotNull(message = "User role is required")
    private UserRole role;
    
    /**
     * Custom validation: either email or phone number must be provided
     */
    public boolean isValid() {
        return (email != null && !email.trim().isEmpty()) || 
               (phoneNumber != null && !phoneNumber.trim().isEmpty());
    }
    
    /**
     * Get the primary identifier (email or phone)
     */
    public String getPrimaryIdentifier() {
        return email != null && !email.trim().isEmpty() ? email : phoneNumber;
    }
}
