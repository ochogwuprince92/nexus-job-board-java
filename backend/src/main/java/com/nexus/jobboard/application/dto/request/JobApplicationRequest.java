package com.nexus.jobboard.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Job application request DTO following SRP
 * - Single responsibility: Handle job application data validation
 */
@Data
public class JobApplicationRequest {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;
    
    // Resume will be handled as MultipartFile in the controller
    // Additional fields can be added here for extended application forms
    private String portfolioUrl;
    
    private String linkedInProfile;
    
    private String githubProfile;
    
    @Size(max = 500, message = "Additional notes must not exceed 500 characters")
    private String additionalNotes;
    
    // Expected salary (optional)
    private String expectedSalary;
    
    // Availability date
    private String availabilityDate;
    
    /**
     * Check if application has minimum required information
     */
    public boolean isValid() {
        return jobId != null;
    }
}
