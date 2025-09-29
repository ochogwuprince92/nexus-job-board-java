package com.nexus.jobboard.domain.repository;

import com.nexus.jobboard.domain.model.Job;
import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Job repository interface following ISP
 * - Contains only job-specific operations
 * - Optimized queries for job search and filtering
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // Basic queries
    Page<Job> findByIsActiveTrue(Pageable pageable);
    
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);
    
    Page<Job> findByCompanyIdAndIsActive(Long companyId, Boolean isActive, Pageable pageable);
    
    Page<Job> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Job> findByPostedById(Long userId, Pageable pageable);
    
    // Filter queries
    Page<Job> findByJobTypeAndIsActiveTrue(JobType jobType, Pageable pageable);
    
    Page<Job> findByExperienceLevelAndIsActiveTrue(ExperienceLevel experienceLevel, Pageable pageable);
    
    Page<Job> findByIsRemoteTrueAndIsActiveTrue(Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<Job> findByLocationContainingIgnoreCaseAndIsActiveTrue(@Param("location") String location, Pageable pageable);
    
    // Full-text search
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.company.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Advanced filtering
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
           "(:minSalary IS NULL OR j.salaryMin >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.salaryMax <= :maxSalary) AND " +
           "(:isRemote IS NULL OR j.isRemote = :isRemote) AND " +
           "(:categoryId IS NULL OR j.category.id = :categoryId)")
    Page<Job> findByFilters(@Param("location") String location,
                           @Param("jobType") JobType jobType,
                           @Param("experienceLevel") ExperienceLevel experienceLevel,
                           @Param("minSalary") BigDecimal minSalary,
                           @Param("maxSalary") BigDecimal maxSalary,
                           @Param("isRemote") Boolean isRemote,
                           @Param("categoryId") Long categoryId,
                           Pageable pageable);
    
    // Skill-based search
    @Query("SELECT DISTINCT j FROM Job j JOIN j.requiredSkills s WHERE " +
           "j.isActive = true AND s.id IN :skillIds")
    Page<Job> findByRequiredSkillsIdIn(@Param("skillIds") List<Long> skillIds, Pageable pageable);
    
    // Maintenance queries
    @Query("SELECT j FROM Job j WHERE j.applicationDeadline < :now AND j.isActive = true")
    List<Job> findExpiredJobs(@Param("now") LocalDateTime now);
    
    // Statistics
    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.isActive = true")
    Long countActiveJobsByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.category.id = :categoryId AND j.isActive = true")
    Long countActiveJobsByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.postedBy.id = :userId AND j.isActive = true")
    Long countActiveJobsByUser(@Param("userId") Long userId);
}
