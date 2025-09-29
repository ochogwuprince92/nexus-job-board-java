package com.nexus.jobboard.application.service;

import com.nexus.jobboard.application.dto.message.EmailMessage;
import com.nexus.jobboard.application.dto.message.JobApplicationMessage;

/**
 * Notification service interface following DIP and SRP
 * - Single responsibility: Handle notification operations
 * - Depends on abstractions for message publishing
 */
public interface NotificationService {
    
    /**
     * Send email notification
     */
    void sendEmail(EmailMessage emailMessage);
    
    /**
     * Send welcome email to new user
     */
    void sendWelcomeEmail(Long userId, String email, String firstName);
    
    /**
     * Send job application confirmation
     */
    void sendJobApplicationConfirmation(JobApplicationMessage applicationMessage);
    
    /**
     * Send application status update notification
     */
    void sendApplicationStatusUpdate(JobApplicationMessage applicationMessage);
    
    /**
     * Send job recommendation notification
     */
    void sendJobRecommendationNotification(Long userId, String email, String jobTitle, Long jobId);
    
    /**
     * Send password reset email
     */
    void sendPasswordResetEmail(String email, String resetToken);
    
    /**
     * Send email verification
     */
    void sendEmailVerification(String email, String verificationToken);
    
    /**
     * Notify employer about new job application
     */
    void notifyEmployerNewApplication(JobApplicationMessage applicationMessage);
}
