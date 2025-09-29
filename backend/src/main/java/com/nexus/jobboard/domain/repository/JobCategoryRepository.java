package com.nexus.jobboard.domain.repository;

import com.nexus.jobboard.domain.model.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Job category repository interface following ISP
 * - Contains only job category-specific operations
 */
@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
    
    // Basic queries
    Optional<JobCategory> findByName(String name);
    
    boolean existsByName(String name);
    
    // Active categories
    List<JobCategory> findByIsActiveTrueOrderByName();
    
    Page<JobCategory> findByIsActive(Boolean isActive, Pageable pageable);
    
    // Search functionality
    @Query("SELECT c FROM JobCategory c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<JobCategory> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Categories with jobs
    @Query("SELECT DISTINCT c FROM JobCategory c JOIN c.jobs j WHERE j.isActive = true")
    List<JobCategory> findCategoriesWithActiveJobs();
    
    @Query("SELECT c FROM JobCategory c WHERE c.isActive = true AND EXISTS " +
           "(SELECT 1 FROM Job j WHERE j.category = c AND j.isActive = true)")
    Page<JobCategory> findActiveCategoriesWithJobs(Pageable pageable);
    
    // Statistics
    @Query("SELECT COUNT(c) FROM JobCategory c WHERE c.isActive = true")
    Long countActiveCategories();
}
