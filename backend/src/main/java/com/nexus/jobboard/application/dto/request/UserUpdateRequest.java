package com.nexus.jobboard.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * User update request DTO following SRP
 * - Single responsibility: Handle user profile update data
 */
@Data
public class UserUpdateRequest {
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    /**
     * Check if any field is provided for update
     */
    public boolean hasUpdates() {
        return firstName != null || lastName != null || email != null || phoneNumber != null;
    }
}
