package com.nexus.jobboard.application.dto.request;

import com.nexus.jobboard.domain.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Application status update request DTO following SRP
 * - Single responsibility: Handle application status update data
 */
@Data
public class ApplicationStatusUpdateRequest {
    
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    // Optional interview scheduling information
    private String interviewDate;
    
    private String interviewTime;
    
    private String interviewLocation;
    
    private String interviewType; // PHONE, VIDEO, IN_PERSON
    
    private String interviewNotes;
    
    /**
     * Check if status transition is valid
     */
    public boolean isValidStatusTransition(ApplicationStatus currentStatus) {
        if (currentStatus == null) {
            return status == ApplicationStatus.PENDING;
        }
        
        return currentStatus.canTransitionTo(status);
    }
}
