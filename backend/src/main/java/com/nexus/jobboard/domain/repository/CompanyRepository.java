package com.nexus.jobboard.domain.repository;

import com.nexus.jobboard.domain.model.Company;
import com.nexus.jobboard.domain.model.CompanySize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Company repository interface following ISP
 * - Contains only company-specific operations
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    // Basic queries
    Optional<Company> findByName(String name);
    
    boolean existsByName(String name);
    
    Page<Company> findByCreatedById(Long createdById, Pageable pageable);
    
    // Verification queries
    Page<Company> findByIsVerified(Boolean isVerified, Pageable pageable);
    
    List<Company> findByIsVerifiedTrue();
    
    @Query("SELECT c FROM Company c WHERE c.isVerified = false ORDER BY c.createdAt ASC")
    Page<Company> findUnverifiedCompaniesOrderByCreatedAt(Pageable pageable);
    
    // Size-based queries
    Page<Company> findBySize(CompanySize size, Pageable pageable);
    
    Page<Company> findBySizeAndIsVerified(CompanySize size, Boolean isVerified, Pageable pageable);
    
    // Industry queries
    Page<Company> findByIndustryContainingIgnoreCase(String industry, Pageable pageable);
    
    @Query("SELECT DISTINCT c.industry FROM Company c WHERE c.industry IS NOT NULL ORDER BY c.industry")
    List<String> findDistinctIndustries();
    
    // Location queries
    Page<Company> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    @Query("SELECT DISTINCT c.location FROM Company c WHERE c.location IS NOT NULL ORDER BY c.location")
    List<String> findDistinctLocations();
    
    // Search functionality
    @Query("SELECT c FROM Company c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.industry) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Company> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Advanced filtering
    @Query("SELECT c FROM Company c WHERE " +
           "(:isVerified IS NULL OR c.isVerified = :isVerified) AND " +
           "(:size IS NULL OR c.size = :size) AND " +
           "(:industry IS NULL OR LOWER(c.industry) LIKE LOWER(CONCAT('%', :industry, '%'))) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<Company> findByFilters(@Param("isVerified") Boolean isVerified,
                               @Param("size") CompanySize size,
                               @Param("industry") String industry,
                               @Param("location") String location,
                               Pageable pageable);
    
    // Statistics
    @Query("SELECT COUNT(c) FROM Company c WHERE c.isVerified = true")
    Long countVerifiedCompanies();
    
    @Query("SELECT COUNT(c) FROM Company c WHERE c.createdBy.id = :userId")
    Long countCompaniesByUser(@Param("userId") Long userId);
    
    // Companies with active jobs
    @Query("SELECT DISTINCT c FROM Company c JOIN c.jobs j WHERE j.isActive = true")
    Page<Company> findCompaniesWithActiveJobs(Pageable pageable);
    
    @Query("SELECT COUNT(DISTINCT c) FROM Company c JOIN c.jobs j WHERE j.isActive = true")
    Long countCompaniesWithActiveJobs();
}
