package com.nexus.jobboard.application.dto.message;

import com.nexus.jobboard.domain.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Job application message DTO following SRP
 * - Single responsibility: Carry job application event data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationMessage {
    
    private Long applicationId;
    private Long jobId;
    private Long applicantId;
    private Long employerId;
    private String jobTitle;
    private String companyName;
    private String applicantName;
    private String applicantEmail;
    private ApplicationStatus status;
    private ApplicationStatus previousStatus;
    private String notes;
    private LocalDateTime eventTime;
    private JobApplicationEventType eventType;
    
    public enum JobApplicationEventType {
        APPLICATION_SUBMITTED,
        APPLICATION_REVIEWED,
        APPLICATION_ACCEPTED,
        APPLICATION_REJECTED,
        APPLICATION_WITHDRAWN,
        INTERVIEW_SCHEDULED
    }
}
