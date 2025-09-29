package com.nexus.jobboard.infrastructure.messaging;

import com.nexus.jobboard.application.dto.message.JobApplicationMessage;
import com.nexus.jobboard.application.service.NotificationService;
import com.nexus.jobboard.domain.repository.UserRepository;
import com.nexus.jobboard.domain.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Job application message consumer following SRP
 * - Single responsibility: Process job application events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobApplicationMessageConsumer {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    
    @RabbitListener(queues = RabbitMQConfig.APPLICATION_PROCESSING_QUEUE)
    public void processJobApplicationMessage(JobApplicationMessage message) {
        log.info("Processing job application message: {} for job: {}", 
                message.getEventType(), message.getJobTitle());
        
        try {
            switch (message.getEventType()) {
                case APPLICATION_SUBMITTED:
                    handleApplicationSubmitted(message);
                    break;
                case APPLICATION_REVIEWED:
                case APPLICATION_ACCEPTED:
                case APPLICATION_REJECTED:
                    handleApplicationStatusUpdate(message);
                    break;
                case APPLICATION_WITHDRAWN:
                    handleApplicationWithdrawn(message);
                    break;
                case INTERVIEW_SCHEDULED:
                    handleInterviewScheduled(message);
                    break;
                default:
                    log.warn("Unknown job application event type: {}", message.getEventType());
            }
            
            log.info("Job application message processed successfully");
        } catch (Exception e) {
            log.error("Failed to process job application message: {}", e.getMessage());
            // In production, implement retry logic or dead letter queue
        }
    }
    
    private void handleApplicationSubmitted(JobApplicationMessage message) {
        log.info("Handling application submitted for job: {}", message.getJobTitle());
        
        // Send confirmation to applicant
        notificationService.sendJobApplicationConfirmation(message);
        
        // Notify employer about new application
        notificationService.notifyEmployerNewApplication(message);
        
        // Additional processing like updating statistics, etc.
        updateJobApplicationStatistics(message);
    }
    
    private void handleApplicationStatusUpdate(JobApplicationMessage message) {
        log.info("Handling application status update: {} for job: {}", 
                message.getStatus(), message.getJobTitle());
        
        // Send status update notification to applicant
        notificationService.sendApplicationStatusUpdate(message);
        
        // Additional processing based on status
        if (message.getStatus().name().equals("ACCEPTED")) {
            handleApplicationAccepted(message);
        } else if (message.getStatus().name().equals("REJECTED")) {
            handleApplicationRejected(message);
        }
    }
    
    private void handleApplicationWithdrawn(JobApplicationMessage message) {
        log.info("Handling application withdrawn for job: {}", message.getJobTitle());
        
        // Notify employer about withdrawal
        // Additional cleanup if needed
    }
    
    private void handleInterviewScheduled(JobApplicationMessage message) {
        log.info("Handling interview scheduled for job: {}", message.getJobTitle());
        
        // Send interview notification
        // This could include calendar invites, etc.
    }
    
    private void handleApplicationAccepted(JobApplicationMessage message) {
        log.info("Processing accepted application for job: {}", message.getJobTitle());
        
        // Additional processing for accepted applications
        // Could trigger job recommendation updates, etc.
    }
    
    private void handleApplicationRejected(JobApplicationMessage message) {
        log.info("Processing rejected application for job: {}", message.getJobTitle());
        
        // Could trigger new job recommendations for the applicant
        triggerJobRecommendations(message.getApplicantId());
    }
    
    private void updateJobApplicationStatistics(JobApplicationMessage message) {
        // Update application statistics
        // This could be cached data for analytics
        log.debug("Updating job application statistics for job: {}", message.getJobId());
    }
    
    private void triggerJobRecommendations(Long applicantId) {
        // Trigger AI-powered job recommendations
        log.debug("Triggering job recommendations for applicant: {}", applicantId);
        
        // This would typically publish to job recommendation queue
        // For now, just log the action
    }
}
