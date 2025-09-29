package com.nexus.jobboard.application.service;

import com.nexus.jobboard.application.dto.request.JobApplicationRequest;
import com.nexus.jobboard.application.dto.request.ApplicationStatusUpdateRequest;
import com.nexus.jobboard.application.dto.response.JobApplicationResponse;
import com.nexus.jobboard.domain.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Job application service interface following DIP and SRP
 * - Single responsibility: Job application business operations
 * - Depends on abstractions for extensibility
 */
public interface JobApplicationService {
    
    /**
     * Apply for a job with resume upload
     */
    JobApplicationResponse applyForJob(JobApplicationRequest request, Long applicantId, MultipartFile resume);
    
    /**
     * Update application status (Employer/Admin only)
     */
    JobApplicationResponse updateApplicationStatus(Long applicationId, 
                                                 ApplicationStatusUpdateRequest request, 
                                                 Long reviewerId);
    
    /**
     * Get application by ID
     */
    Optional<JobApplicationResponse> getApplicationById(Long applicationId);
    
    /**
     * Get applications for a specific job (Employer/Admin)
     */
    Page<JobApplicationResponse> getApplicationsByJob(Long jobId, Pageable pageable);
    
    /**
     * Get applications by applicant (Job Seeker)
     */
    Page<JobApplicationResponse> getApplicationsByApplicant(Long applicantId, Pageable pageable);
    
    /**
     * Get applications for company jobs (Employer)
     */
    Page<JobApplicationResponse> getApplicationsByCompany(Long companyId, Pageable pageable);
    
    /**
     * Get applications by status
     */
    Page<JobApplicationResponse> getApplicationsByStatus(ApplicationStatus status, Pageable pageable);
    
    /**
     * Withdraw application (Job Seeker only)
     */
    void withdrawApplication(Long applicationId, Long applicantId);
    
    /**
     * Check if user has already applied for job
     */
    boolean hasUserAppliedForJob(Long jobId, Long applicantId);
    
    /**
     * Get application statistics
     */
    Long getApplicationCountByJob(Long jobId);
    
    Long getApplicationCountByApplicant(Long applicantId);
    
    /**
     * Bulk update applications (Admin only)
     */
    void bulkUpdateApplicationStatus(Long jobId, ApplicationStatus status, String notes);
}
