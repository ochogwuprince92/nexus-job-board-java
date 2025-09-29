package com.nexus.jobboard.application.service.impl;

import com.nexus.jobboard.application.dto.message.JobApplicationMessage;
import com.nexus.jobboard.application.dto.request.JobApplicationRequest;
import com.nexus.jobboard.application.dto.request.ApplicationStatusUpdateRequest;
import com.nexus.jobboard.application.dto.response.JobApplicationResponse;
import com.nexus.jobboard.application.mapper.JobApplicationMapper;
import com.nexus.jobboard.application.service.JobApplicationService;
import com.nexus.jobboard.application.service.FileStorageService;
import com.nexus.jobboard.application.service.NotificationService;
import com.nexus.jobboard.domain.model.JobApplication;
import com.nexus.jobboard.domain.model.Job;
import com.nexus.jobboard.domain.model.User;
import com.nexus.jobboard.domain.model.ApplicationStatus;
import com.nexus.jobboard.domain.repository.JobApplicationRepository;
import com.nexus.jobboard.domain.repository.JobRepository;
import com.nexus.jobboard.domain.repository.UserRepository;
import com.nexus.jobboard.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Job application service implementation following SOLID principles
 * 
 * SRP: Handles only job application business logic
 * OCP: Open for extension through interface implementation
 * LSP: Substitutable for JobApplicationService interface
 * ISP: Depends only on specific interfaces it needs
 * DIP: Depends on abstractions (repositories, services)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {
    
    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final JobApplicationMapper jobApplicationMapper;
    
    @Override
    public JobApplicationResponse applyForJob(JobApplicationRequest request, Long applicantId, MultipartFile resume) {
        log.info("Processing job application for job: {} by user: {}", request.getJobId(), applicantId);
        
        // Validate job exists and is active
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", request.getJobId()));
        
        if (!job.canAcceptApplications()) {
            throw new IllegalStateException("Job is not accepting applications");
        }
        
        // Validate user exists
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", applicantId));
        
        // Check if user has already applied
        if (jobApplicationRepository.existsByJobIdAndApplicantId(request.getJobId(), applicantId)) {
            throw new IllegalStateException("User has already applied for this job");
        }
        
        // Store resume
        String resumeUrl = fileStorageService.storeResume(resume, applicantId);
        
        // Create job application
        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .coverLetter(request.getCoverLetter())
                .resumeUrl(resumeUrl)
                .status(ApplicationStatus.PENDING)
                .build();
        
        JobApplication savedApplication = jobApplicationRepository.save(application);
        
        // Send notifications
        sendApplicationNotifications(savedApplication, JobApplicationMessage.JobApplicationEventType.APPLICATION_SUBMITTED);
        
        log.info("Job application created successfully: {}", savedApplication.getId());
        return jobApplicationMapper.toResponse(savedApplication);
    }
    
    @Override
    public JobApplicationResponse updateApplicationStatus(Long applicationId, 
                                                        ApplicationStatusUpdateRequest request, 
                                                        Long reviewerId) {
        log.info("Updating application status: {} to {}", applicationId, request.getStatus());
        
        JobApplication application = findApplicationById(applicationId);
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", reviewerId));
        
        // Validate status transition
        if (!request.isValidStatusTransition(application.getStatus())) {
            throw new IllegalStateException("Invalid status transition from " + 
                    application.getStatus() + " to " + request.getStatus());
        }
        
        ApplicationStatus previousStatus = application.getStatus();
        application.updateStatus(request.getStatus(), reviewer);
        application.setNotes(request.getNotes());
        
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        
        // Send status update notifications
        sendStatusUpdateNotifications(updatedApplication, previousStatus);
        
        log.info("Application status updated successfully: {}", applicationId);
        return jobApplicationMapper.toResponse(updatedApplication);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<JobApplicationResponse> getApplicationById(Long applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .map(jobApplicationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsByJob(Long jobId, Pageable pageable) {
        return jobApplicationRepository.findByJobId(jobId, pageable)
                .map(jobApplicationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsByApplicant(Long applicantId, Pageable pageable) {
        return jobApplicationRepository.findByApplicantId(applicantId, pageable)
                .map(jobApplicationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsByCompany(Long companyId, Pageable pageable) {
        return jobApplicationRepository.findByCompanyId(companyId, pageable)
                .map(jobApplicationMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsByStatus(ApplicationStatus status, Pageable pageable) {
        return jobApplicationRepository.findByStatus(status, pageable)
                .map(jobApplicationMapper::toResponse);
    }
    
    @Override
    public void withdrawApplication(Long applicationId, Long applicantId) {
        log.info("Withdrawing application: {} by user: {}", applicationId, applicantId);
        
        JobApplication application = findApplicationById(applicationId);
        
        // Verify ownership
        if (!application.isOwnedBy(userRepository.findById(applicantId).orElse(null))) {
            throw new IllegalStateException("User cannot withdraw this application");
        }
        
        // Check if application can be withdrawn
        if (application.isFinalStatus()) {
            throw new IllegalStateException("Cannot withdraw application in final status");
        }
        
        application.withdraw();
        jobApplicationRepository.save(application);
        
        // Send withdrawal notifications
        sendApplicationNotifications(application, JobApplicationMessage.JobApplicationEventType.APPLICATION_WITHDRAWN);
        
        log.info("Application withdrawn successfully: {}", applicationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserAppliedForJob(Long jobId, Long applicantId) {
        return jobApplicationRepository.existsByJobIdAndApplicantId(jobId, applicantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getApplicationCountByJob(Long jobId) {
        return jobApplicationRepository.countApplicationsByJob(jobId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getApplicationCountByApplicant(Long applicantId) {
        return jobApplicationRepository.countApplicationsByApplicant(applicantId);
    }
    
    @Override
    public void bulkUpdateApplicationStatus(Long jobId, ApplicationStatus status, String notes) {
        log.info("Bulk updating applications for job: {} to status: {}", jobId, status);
        
        Page<JobApplication> applications = jobApplicationRepository.findByJobId(jobId, Pageable.unpaged());
        
        applications.getContent().forEach(application -> {
            if (!application.isFinalStatus()) {
                application.setStatus(status);
                application.setNotes(notes);
                application.setReviewedAt(LocalDateTime.now());
                jobApplicationRepository.save(application);
                
                // Send individual notifications
                sendStatusUpdateNotifications(application, application.getStatus());
            }
        });
        
        log.info("Bulk update completed for job: {}", jobId);
    }
    
    private JobApplication findApplicationById(Long applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", applicationId));
    }
    
    private void sendApplicationNotifications(JobApplication application, 
                                            JobApplicationMessage.JobApplicationEventType eventType) {
        JobApplicationMessage message = JobApplicationMessage.builder()
                .applicationId(application.getId())
                .jobId(application.getJob().getId())
                .applicantId(application.getApplicant().getId())
                .employerId(application.getJob().getPostedBy().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .applicantName(application.getApplicant().getFullName())
                .applicantEmail(application.getApplicant().getEmail())
                .status(application.getStatus())
                .eventType(eventType)
                .eventTime(LocalDateTime.now())
                .build();
        
        switch (eventType) {
            case APPLICATION_SUBMITTED:
                notificationService.sendJobApplicationConfirmation(message);
                notificationService.notifyEmployerNewApplication(message);
                break;
            case APPLICATION_WITHDRAWN:
                // Could send withdrawal notification to employer
                break;
            default:
                log.debug("No specific notification for event type: {}", eventType);
        }
    }
    
    private void sendStatusUpdateNotifications(JobApplication application, ApplicationStatus previousStatus) {
        JobApplicationMessage message = JobApplicationMessage.builder()
                .applicationId(application.getId())
                .jobId(application.getJob().getId())
                .applicantId(application.getApplicant().getId())
                .employerId(application.getJob().getPostedBy().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .applicantName(application.getApplicant().getFullName())
                .applicantEmail(application.getApplicant().getEmail())
                .status(application.getStatus())
                .previousStatus(previousStatus)
                .notes(application.getNotes())
                .eventType(determineEventType(application.getStatus()))
                .eventTime(LocalDateTime.now())
                .build();
        
        notificationService.sendApplicationStatusUpdate(message);
    }
    
    private JobApplicationMessage.JobApplicationEventType determineEventType(ApplicationStatus status) {
        return switch (status) {
            case REVIEWING -> JobApplicationMessage.JobApplicationEventType.APPLICATION_REVIEWED;
            case ACCEPTED -> JobApplicationMessage.JobApplicationEventType.APPLICATION_ACCEPTED;
            case REJECTED -> JobApplicationMessage.JobApplicationEventType.APPLICATION_REJECTED;
            case INTERVIEW_SCHEDULED -> JobApplicationMessage.JobApplicationEventType.INTERVIEW_SCHEDULED;
            default -> JobApplicationMessage.JobApplicationEventType.APPLICATION_REVIEWED;
        };
    }
}
