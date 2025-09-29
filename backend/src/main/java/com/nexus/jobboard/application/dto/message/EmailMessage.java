package com.nexus.jobboard.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Email message DTO following SRP
 * - Single responsibility: Carry email data between services
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    
    private String to;
    private String from;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
    private String htmlContent;
    private String textContent;
    private EmailType emailType;
    private Long userId;
    private Long relatedEntityId;
    private LocalDateTime scheduledAt;
    
    public enum EmailType {
        WELCOME,
        JOB_APPLICATION_CONFIRMATION,
        JOB_APPLICATION_STATUS_UPDATE,
        NEW_JOB_RECOMMENDATION,
        PASSWORD_RESET,
        EMAIL_VERIFICATION,
        JOB_POSTED_CONFIRMATION,
        APPLICATION_RECEIVED_NOTIFICATION
    }
}
