package com.nexus.jobboard.presentation.controller;

import com.nexus.jobboard.application.dto.request.JobCreateRequest;
import com.nexus.jobboard.application.dto.request.JobUpdateRequest;
import com.nexus.jobboard.application.dto.response.JobResponse;
import com.nexus.jobboard.application.service.JobService;
import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * Job controller following SRP
 * - Single responsibility: Handle job-related endpoints
 * - Depends on service abstractions (DIP)
 */
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Jobs", description = "Job management endpoints")
public class JobController {
    
    private final JobService jobService;
    
    @PostMapping
    @Operation(summary = "Create new job", description = "Create a new job posting (Employer/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobCreateRequest request,
            Authentication authentication) {
        log.info("Creating new job with title: {}", request.getTitle());
        
        if (!request.isValidSalaryRange()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!request.isValidApplicationDeadline()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Extract user ID from authentication (this would be implemented in a real scenario)
        Long employerId = extractUserIdFromAuthentication(authentication);
        
        JobResponse response = jobService.createJob(request, employerId);
        log.info("Job created successfully with ID: {}", response.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all active jobs", description = "Retrieve all active job postings with pagination")
    public ResponseEntity<Page<JobResponse>> getAllActiveJobs(Pageable pageable) {
        log.info("Getting all active jobs with pagination");
        
        Page<JobResponse> jobs = jobService.getAllActiveJobs(pageable);
        log.info("Retrieved {} active jobs", jobs.getTotalElements());
        
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/{jobId}")
    @Operation(summary = "Get job by ID", description = "Retrieve job details by ID")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long jobId) {
        log.info("Getting job by ID: {}", jobId);
        
        return jobService.getJobById(jobId)
                .map(job -> {
                    log.info("Job retrieved successfully: {}", jobId);
                    return ResponseEntity.ok(job);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{jobId}")
    @Operation(summary = "Update job", description = "Update job posting (Owner/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @jobService.getJobById(#jobId).orElse(null)?.postedBy?.id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobUpdateRequest request,
            Authentication authentication) {
        log.info("Updating job with ID: {}", jobId);
        
        if (!request.hasUpdates()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!request.isValidSalaryRange()) {
            return ResponseEntity.badRequest().build();
        }
        
        Long employerId = extractUserIdFromAuthentication(authentication);
        
        JobResponse response = jobService.updateJob(jobId, request, employerId);
        log.info("Job updated successfully: {}", jobId);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search jobs", description = "Search jobs by title, description, or company name")
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam String query,
            Pageable pageable) {
        log.info("Searching jobs with query: {}", query);
        
        Page<JobResponse> jobs = jobService.searchJobs(query, pageable);
        log.info("Found {} jobs matching query: {}", jobs.getTotalElements(), query);
        
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter jobs", description = "Filter jobs by various criteria")
    public ResponseEntity<Page<JobResponse>> filterJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) Boolean isRemote,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {
        log.info("Filtering jobs with criteria - location: {}, jobType: {}, experienceLevel: {}", 
                location, jobType, experienceLevel);
        
        Page<JobResponse> jobs = jobService.getJobsByFilters(
                location, jobType, experienceLevel, minSalary, maxSalary, isRemote, categoryId, pageable);
        log.info("Found {} jobs matching filters", jobs.getTotalElements());
        
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get jobs by company", description = "Retrieve jobs posted by a specific company")
    public ResponseEntity<Page<JobResponse>> getJobsByCompany(
            @PathVariable Long companyId,
            Pageable pageable) {
        log.info("Getting jobs for company: {}", companyId);
        
        Page<JobResponse> jobs = jobService.getJobsByCompany(companyId, pageable);
        log.info("Retrieved {} jobs for company: {}", jobs.getTotalElements(), companyId);
        
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get jobs by category", description = "Retrieve jobs in a specific category")
    public ResponseEntity<Page<JobResponse>> getJobsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        log.info("Getting jobs for category: {}", categoryId);
        
        Page<JobResponse> jobs = jobService.getJobsByCategory(categoryId, pageable);
        log.info("Retrieved {} jobs for category: {}", jobs.getTotalElements(), categoryId);
        
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/skills")
    @Operation(summary = "Get jobs by skills", description = "Find jobs that require specific skills")
    public ResponseEntity<Page<JobResponse>> getJobsBySkills(
            @RequestParam List<Long> skillIds,
            Pageable pageable) {
        log.info("Getting jobs for skills: {}", skillIds);
        
        Page<JobResponse> jobs = jobService.getJobsBySkills(skillIds, pageable);
        log.info("Retrieved {} jobs for skills: {}", jobs.getTotalElements(), skillIds);
        
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/my-jobs")
    @Operation(summary = "Get my posted jobs", description = "Retrieve jobs posted by the authenticated user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<JobResponse>> getMyJobs(
            Authentication authentication,
            Pageable pageable) {
        Long userId = extractUserIdFromAuthentication(authentication);
        log.info("Getting jobs posted by user: {}", userId);
        
        Page<JobResponse> jobs = jobService.getJobsByUser(userId, pageable);
        log.info("Retrieved {} jobs for user: {}", jobs.getTotalElements(), userId);
        
        return ResponseEntity.ok(jobs);
    }
    
    @PutMapping("/{jobId}/deactivate")
    @Operation(summary = "Deactivate job", description = "Deactivate job posting (Owner/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @jobService.getJobById(#jobId).orElse(null)?.postedBy?.id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deactivateJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        log.info("Deactivating job: {}", jobId);
        
        Long employerId = extractUserIdFromAuthentication(authentication);
        jobService.deactivateJob(jobId, employerId);
        log.info("Job deactivated successfully: {}", jobId);
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{jobId}/activate")
    @Operation(summary = "Activate job", description = "Activate job posting (Owner/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @jobService.getJobById(#jobId).orElse(null)?.postedBy?.id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> activateJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        log.info("Activating job: {}", jobId);
        
        Long employerId = extractUserIdFromAuthentication(authentication);
        jobService.activateJob(jobId, employerId);
        log.info("Job activated successfully: {}", jobId);
        
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{jobId}")
    @Operation(summary = "Delete job", description = "Delete job posting (Owner/Admin only)")
    @PreAuthorize("hasRole('ADMIN') or @jobService.getJobById(#jobId).orElse(null)?.postedBy?.id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId,
            Authentication authentication) {
        log.info("Deleting job: {}", jobId);
        
        Long employerId = extractUserIdFromAuthentication(authentication);
        jobService.deleteJob(jobId, employerId);
        log.info("Job deleted successfully: {}", jobId);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/recommendations")
    @Operation(summary = "Get recommended jobs", description = "Get AI-powered job recommendations for the user")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<JobResponse>> getRecommendedJobs(
            Authentication authentication,
            Pageable pageable) {
        Long userId = extractUserIdFromAuthentication(authentication);
        log.info("Getting recommended jobs for user: {}", userId);
        
        Page<JobResponse> jobs = jobService.getRecommendedJobs(userId, pageable);
        log.info("Retrieved {} recommended jobs for user: {}", jobs.getTotalElements(), userId);
        
        return ResponseEntity.ok(jobs);
    }
    
    // Helper method to extract user ID from authentication
    // In a real implementation, this would be properly implemented
    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - in real implementation, you'd extract the user ID
        // from the JWT token or UserDetails
        return 1L; // Placeholder
    }
}
