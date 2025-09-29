package com.nexus.jobboard.application.service;

import com.nexus.jobboard.application.dto.request.JobCreateRequest;
import com.nexus.jobboard.application.dto.request.JobUpdateRequest;
import com.nexus.jobboard.application.dto.request.JobSearchRequest;
import com.nexus.jobboard.application.dto.response.JobResponse;
import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Job service interface following DIP and SRP
 * - Depends on abstractions, not concrete implementations
 * - Single responsibility: Job-related business operations
 */
public interface JobService {
    
    /**
     * Create a new job posting
     */
    JobResponse createJob(JobCreateRequest request, Long employerId);
    
    /**
     * Update existing job posting
     */
    JobResponse updateJob(Long jobId, JobUpdateRequest request, Long employerId);
    
    /**
     * Get job by ID
     */
    Optional<JobResponse> getJobById(Long jobId);
    
    /**
     * Get all active jobs with pagination
     */
    Page<JobResponse> getAllActiveJobs(Pageable pageable);
    
    /**
     * Get jobs by company
     */
    Page<JobResponse> getJobsByCompany(Long companyId, Pageable pageable);
    
    /**
     * Get jobs by category
     */
    Page<JobResponse> getJobsByCategory(Long categoryId, Pageable pageable);
    
    /**
     * Get jobs posted by specific user
     */
    Page<JobResponse> getJobsByUser(Long userId, Pageable pageable);
    
    /**
     * Search jobs with full-text search
     */
    Page<JobResponse> searchJobs(String searchTerm, Pageable pageable);
    
    /**
     * Advanced job filtering
     */
    Page<JobResponse> getJobsByFilters(String location, JobType jobType, 
                                     ExperienceLevel experienceLevel,
                                     BigDecimal minSalary, BigDecimal maxSalary,
                                     Boolean isRemote, Long categoryId,
                                     Pageable pageable);
    
    /**
     * Find jobs by required skills
     */
    Page<JobResponse> getJobsBySkills(List<Long> skillIds, Pageable pageable);
    
    /**
     * Deactivate job posting
     */
    void deactivateJob(Long jobId, Long employerId);
    
    /**
     * Activate job posting
     */
    void activateJob(Long jobId, Long employerId);
    
    /**
     * Delete job posting (soft delete by deactivation)
     */
    void deleteJob(Long jobId, Long employerId);
    
    /**
     * Get active job count by company
     */
    Long getActiveJobCountByCompany(Long companyId);
    
    /**
     * Get active job count by category
     */
    Long getActiveJobCountByCategory(Long categoryId);
    
    /**
     * Get active job count by user
     */
    Long getActiveJobCountByUser(Long userId);
    
    /**
     * Process expired jobs (background task)
     */
    void processExpiredJobs();
    
    /**
     * Get recommended jobs for a user (AI-powered)
     */
    Page<JobResponse> getRecommendedJobs(Long userId, Pageable pageable);
}
