package com.nexus.jobboard.application.service.impl;

import com.nexus.jobboard.application.dto.message.EmailMessage;
import com.nexus.jobboard.application.dto.message.JobApplicationMessage;
import com.nexus.jobboard.application.service.NotificationService;
import com.nexus.jobboard.infrastructure.messaging.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Notification service implementation following SOLID principles
 * 
 * SRP: Handles only notification-related business logic
 * OCP: Open for extension through interface implementation
 * LSP: Substitutable for NotificationService interface
 * ISP: Depends only on specific interfaces it needs
 * DIP: Depends on abstractions (RabbitTemplate)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("Publishing email message to queue: {}", emailMessage.getTo());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailMessage
        );
        
        log.info("Email message published successfully");
    }
    
    @Override
    public void sendWelcomeEmail(Long userId, String email, String firstName) {
        log.info("Sending welcome email to user: {}", email);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", firstName);
        variables.put("loginUrl", "http://localhost:3000/login");
        
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("Welcome to Nexus Job Board!")
                .templateName("welcome")
                .templateVariables(variables)
                .emailType(EmailMessage.EmailType.WELCOME)
                .userId(userId)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        sendEmail(emailMessage);
    }
    
    @Override
    public void sendJobApplicationConfirmation(JobApplicationMessage applicationMessage) {
        log.info("Sending job application confirmation to: {}", applicationMessage.getApplicantEmail());
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantName", applicationMessage.getApplicantName());
        variables.put("jobTitle", applicationMessage.getJobTitle());
        variables.put("companyName", applicationMessage.getCompanyName());
        variables.put("applicationId", applicationMessage.getApplicationId());
        variables.put("dashboardUrl", "http://localhost:3000/dashboard");
        
        EmailMessage emailMessage = EmailMessage.builder()
                .to(applicationMessage.getApplicantEmail())
                .subject("Application Submitted: " + applicationMessage.getJobTitle())
                .templateName("job-application-confirmation")
                .templateVariables(variables)
                .emailType(EmailMessage.EmailType.JOB_APPLICATION_CONFIRMATION)
                .userId(applicationMessage.getApplicantId())
                .relatedEntityId(applicationMessage.getApplicationId())
                .scheduledAt(LocalDateTime.now())
                .build();
        
        sendEmail(emailMessage);
    }
    
    @Override
    public void sendApplicationStatusUpdate(JobApplicationMessage applicationMessage) {
        log.info("Sending application status update to: {}", applicationMessage.getApplicantEmail());
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantName", applicationMessage.getApplicantName());
        variables.put("jobTitle", applicationMessage.getJobTitle());
        variables.put("companyName", applicationMessage.getCompanyName());
        variables.put("status", applicationMessage.getStatus().name());
        variables.put("statusDescription", applicationMessage.getStatus().getDescription());
        variables.put("notes", applicationMessage.getNotes());
        variables.put("dashboardUrl", "http://localhost:3000/dashboard");
        
        String subject = String.format("Application Update: %s - %s", 
                applicationMessage.getJobTitle(), 
                applicationMessage.getStatus().getDescription());
        
        EmailMessage emailMessage = EmailMessage.builder()
                .to(applicationMessage.getApplicantEmail())
                .subject(subject)
                .templateName("application-status-update")
                .templateVariables(variables)
                .emailType(EmailMessage.EmailType.JOB_APPLICATION_STATUS_UPDATE)
                .userId(applicationMessage.getApplicantId())
                .relatedEntityId(applicationMessage.getApplicationId())
                .scheduledAt(LocalDateTime.now())
                .build();
        
        sendEmail(emailMessage);
    }
    
    @Override
    public void sendJobRecommendationNotification(Long userId, String email, String jobTitle, Long jobId) {
        log.info("Sending job recommendation notification to: {}", email);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobTitle", jobTitle);
        variables.put("jobUrl", "http://localhost:3000/jobs/" + jobId);
        variables.put("dashboardUrl", "http://localhost:3000/dashboard");
        
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("New Job Recommendation: " + jobTitle)
                .templateName("job-recommendation")
                .templateVariables(variables)
                .emailType(EmailMessage.EmailType.NEW_JOB_RECOMMENDATION)
                .userId(userId)
                .relatedEntityId(jobId)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        sendEmail(emailMessage);
    }
    
    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        log.info("Sending password reset email to: {}", email);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("resetUrl", "http://localhost:3000/reset-password?token=" + resetToken);
        variables.put("expirationTime", "24 hours");
        
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("Password Reset Request")
                .templateName("password-reset")
                .templateVariables(variables)
                .emailType(EmailMessage.EmailType.PASSWORD_RESET)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        sendEmail(emailMessage);
    }
    
    @Override
    public void sendEmailVerification(String email, String verificationToken) {
        log.info("Sending email verification to: {}", email);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("verificationUrl", "http://localhost:3000/verify-email?token=" + verificationToken);
        
        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject("Verify Your Email Address")
                .templateName("email-verification")
                .templateVariables(variables)
                .emailType(EmailMessage.EmailType.EMAIL_VERIFICATION)
                .scheduledAt(LocalDateTime.now())
                .build();
        
        sendEmail(emailMessage);
    }
    
    @Override
    public void notifyEmployerNewApplication(JobApplicationMessage applicationMessage) {
        log.info("Notifying employer about new application for job: {}", applicationMessage.getJobTitle());
        
        // This would typically get the employer's email from the database
        // For now, we'll publish to a queue for processing
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_EXCHANGE,
                RabbitMQConfig.JOB_APPLICATION_ROUTING_KEY,
                applicationMessage
        );
        
        log.info("Employer notification published successfully");
    }
}
