package com.nexus.jobboard.presentation.controller;

import com.nexus.jobboard.application.dto.request.JobApplicationRequest;
import com.nexus.jobboard.application.dto.request.ApplicationStatusUpdateRequest;
import com.nexus.jobboard.application.dto.response.JobApplicationResponse;
import com.nexus.jobboard.application.service.JobApplicationService;
import com.nexus.jobboard.domain.model.ApplicationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Job application controller following SRP
 * - Single responsibility: Handle job application endpoints
 * - Depends on service abstractions (DIP)
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Job Applications", description = "Job application management endpoints")
public class JobApplicationController {
    
    private final JobApplicationService jobApplicationService;
    
    @PostMapping
    @Operation(summary = "Apply for a job", description = "Submit job application with resume")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<JobApplicationResponse> applyForJob(
            @Valid @RequestPart("application") JobApplicationRequest request,
            @RequestPart("resume") MultipartFile resume,
            Authentication authentication) {
        log.info("Job application request for job: {}", request.getJobId());
        
        Long applicantId = extractUserIdFromAuthentication(authentication);
        
        // Check if user has already applied
        if (jobApplicationService.hasUserAppliedForJob(request.getJobId(), applicantId)) {
            return ResponseEntity.badRequest().build();
        }
        
        JobApplicationResponse response = jobApplicationService.applyForJob(request, applicantId, resume);
        log.info("Job application submitted successfully: {}", response.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{applicationId}")
    @Operation(summary = "Get application by ID", description = "Retrieve application details")
    @PreAuthorize("hasRole('ADMIN') or @jobApplicationService.getApplicationById(#applicationId).orElse(null)?.applicant?.id == authentication.principal.id or @jobApplicationService.getApplicationById(#applicationId).orElse(null)?.job?.postedBy?.id == authentication.principal.id")
    public ResponseEntity<JobApplicationResponse> getApplicationById(@PathVariable Long applicationId) {
        log.info("Getting application by ID: {}", applicationId);
        
        return jobApplicationService.getApplicationById(applicationId)
                .map(application -> {
                    log.info("Application retrieved successfully: {}", applicationId);
                    return ResponseEntity.ok(application);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{applicationId}/status")
    @Operation(summary = "Update application status", description = "Update application status (Employer/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @jobApplicationService.getApplicationById(#applicationId).orElse(null)?.job?.postedBy?.id == authentication.principal.id")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            Authentication authentication) {
        log.info("Updating application status: {} to {}", applicationId, request.getStatus());
        
        Long reviewerId = extractUserIdFromAuthentication(authentication);
        
        JobApplicationResponse response = jobApplicationService.updateApplicationStatus(
                applicationId, request, reviewerId);
        log.info("Application status updated successfully: {}", applicationId);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get applications for job", description = "Get all applications for a specific job (Employer/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @jobService.getJobById(#jobId).orElse(null)?.postedBy?.id == authentication.principal.id")
    public ResponseEntity<Page<JobApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId,
            Pageable pageable) {
        log.info("Getting applications for job: {}", jobId);
        
        Page<JobApplicationResponse> applications = jobApplicationService.getApplicationsByJob(jobId, pageable);
        log.info("Retrieved {} applications for job: {}", applications.getTotalElements(), jobId);
        
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/my-applications")
    @Operation(summary = "Get my applications", description = "Get applications submitted by the authenticated user")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Page<JobApplicationResponse>> getMyApplications(
            Authentication authentication,
            Pageable pageable) {
        Long applicantId = extractUserIdFromAuthentication(authentication);
        log.info("Getting applications for user: {}", applicantId);
        
        Page<JobApplicationResponse> applications = jobApplicationService.getApplicationsByApplicant(applicantId, pageable);
        log.info("Retrieved {} applications for user: {}", applications.getTotalElements(), applicantId);
        
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get applications for company", description = "Get all applications for company jobs (Employer/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @companyService.getCompanyById(#companyId).orElse(null)?.createdBy?.id == authentication.principal.id")
    public ResponseEntity<Page<JobApplicationResponse>> getApplicationsByCompany(
            @PathVariable Long companyId,
            Pageable pageable) {
        log.info("Getting applications for company: {}", companyId);
        
        Page<JobApplicationResponse> applications = jobApplicationService.getApplicationsByCompany(companyId, pageable);
        log.info("Retrieved {} applications for company: {}", applications.getTotalElements(), companyId);
        
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get applications by status", description = "Get applications filtered by status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<JobApplicationResponse>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status,
            Pageable pageable) {
        log.info("Getting applications with status: {}", status);
        
        Page<JobApplicationResponse> applications = jobApplicationService.getApplicationsByStatus(status, pageable);
        log.info("Retrieved {} applications with status: {}", applications.getTotalElements(), status);
        
        return ResponseEntity.ok(applications);
    }
    
    @PutMapping("/{applicationId}/withdraw")
    @Operation(summary = "Withdraw application", description = "Withdraw job application (Job Seeker only)")
    @PreAuthorize("hasRole('JOB_SEEKER') and @jobApplicationService.getApplicationById(#applicationId).orElse(null)?.applicant?.id == authentication.principal.id")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {
        log.info("Withdrawing application: {}", applicationId);
        
        Long applicantId = extractUserIdFromAuthentication(authentication);
        jobApplicationService.withdrawApplication(applicationId, applicantId);
        log.info("Application withdrawn successfully: {}", applicationId);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats/job/{jobId}")
    @Operation(summary = "Get application count for job", description = "Get total application count for a job")
    public ResponseEntity<Long> getApplicationCountByJob(@PathVariable Long jobId) {
        log.info("Getting application count for job: {}", jobId);
        
        Long count = jobApplicationService.getApplicationCountByJob(jobId);
        log.info("Application count for job {}: {}", jobId, count);
        
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/my-applications-count")
    @Operation(summary = "Get my application count", description = "Get total application count for authenticated user")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Long> getMyApplicationCount(Authentication authentication) {
        Long applicantId = extractUserIdFromAuthentication(authentication);
        log.info("Getting application count for user: {}", applicantId);
        
        Long count = jobApplicationService.getApplicationCountByApplicant(applicantId);
        log.info("Application count for user {}: {}", applicantId, count);
        
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/job/{jobId}/bulk-update")
    @Operation(summary = "Bulk update applications", description = "Bulk update all applications for a job (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkUpdateApplications(
            @PathVariable Long jobId,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String notes) {
        log.info("Bulk updating applications for job: {} to status: {}", jobId, status);
        
        jobApplicationService.bulkUpdateApplicationStatus(jobId, status, notes);
        log.info("Bulk update completed for job: {}", jobId);
        
        return ResponseEntity.ok().build();
    }
    
    // Helper method to extract user ID from authentication
    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - in real implementation, you'd extract the user ID
        // from the JWT token or UserDetails
        return 1L; // Placeholder
    }
}
