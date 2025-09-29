package com.nexus.jobboard.domain.repository;

import com.nexus.jobboard.domain.model.JobApplication;
import com.nexus.jobboard.domain.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Job application repository interface following ISP
 * - Contains only job application-specific operations
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // Basic queries
    Page<JobApplication> findByJobId(Long jobId, Pageable pageable);
    
    Page<JobApplication> findByApplicantId(Long applicantId, Pageable pageable);
    
    Page<JobApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    
    // Unique application check
    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);
    
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
    
    // Company-specific queries
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.company.id = :companyId")
    Page<JobApplication> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);
    
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.company.id = :companyId AND ja.status = :status")
    Page<JobApplication> findByCompanyIdAndStatus(@Param("companyId") Long companyId, 
                                                 @Param("status") ApplicationStatus status, 
                                                 Pageable pageable);
    
    // Employer queries (jobs posted by specific user)
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.postedBy.id = :employerId")
    Page<JobApplication> findByEmployerId(@Param("employerId") Long employerId, Pageable pageable);
    
    // Time-based queries
    @Query("SELECT ja FROM JobApplication ja WHERE ja.appliedAt BETWEEN :startDate AND :endDate")
    Page<JobApplication> findByAppliedAtBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               Pageable pageable);
    
    @Query("SELECT ja FROM JobApplication ja WHERE ja.appliedAt >= :date")
    List<JobApplication> findRecentApplications(@Param("date") LocalDateTime date);
    
    // Statistics
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.id = :jobId")
    Long countApplicationsByJob(@Param("jobId") Long jobId);
    
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.applicant.id = :applicantId")
    Long countApplicationsByApplicant(@Param("applicantId") Long applicantId);
    
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.company.id = :companyId")
    Long countApplicationsByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.id = :jobId AND ja.status = :status")
    Long countApplicationsByJobAndStatus(@Param("jobId") Long jobId, @Param("status") ApplicationStatus status);
    
    // Status-based queries
    @Query("SELECT ja FROM JobApplication ja WHERE ja.status IN :statuses")
    Page<JobApplication> findByStatusIn(@Param("statuses") List<ApplicationStatus> statuses, Pageable pageable);
    
    @Query("SELECT ja FROM JobApplication ja WHERE ja.applicant.id = :applicantId AND ja.status IN :statuses")
    Page<JobApplication> findByApplicantIdAndStatusIn(@Param("applicantId") Long applicantId,
                                                     @Param("statuses") List<ApplicationStatus> statuses,
                                                     Pageable pageable);
}
