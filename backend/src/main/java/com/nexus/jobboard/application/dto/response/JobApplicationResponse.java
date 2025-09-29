package com.nexus.jobboard.application.dto.response;

import com.nexus.jobboard.domain.model.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Job application response DTO following SRP
 * - Single responsibility: Present job application data to clients
 */
@Data
@Builder
public class JobApplicationResponse {
    
    private Long id;
    private JobResponse job;
    private UserResponse applicant;
    private String coverLetter;
    private String resumeUrl;
    private ApplicationStatus status;
    private String notes;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private UserResponse reviewedBy;
    
    // Additional application fields
    private String portfolioUrl;
    private String linkedInProfile;
    private String githubProfile;
    private String additionalNotes;
    private String expectedSalary;
    private String availabilityDate;
    
    // Interview information
    private String interviewDate;
    private String interviewTime;
    private String interviewLocation;
    private String interviewType;
    private String interviewNotes;
    
    // Computed fields
    public String getStatusDescription() {
        return status != null ? status.getDescription() : null;
    }
    
    public boolean isFinalStatus() {
        return status != null && status.isFinalStatus();
    }
    
    public boolean canBeWithdrawn() {
        return status != null && !status.isFinalStatus() && status != ApplicationStatus.WITHDRAWN;
    }
    
    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }
    
    public boolean isPositiveStatus() {
        return status != null && status.isPositiveStatus();
    }
    
    public long getDaysSinceApplied() {
        if (appliedAt == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(appliedAt.toLocalDate(), LocalDateTime.now().toLocalDate());
    }
}
