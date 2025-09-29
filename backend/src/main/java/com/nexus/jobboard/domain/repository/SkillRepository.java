package com.nexus.jobboard.domain.repository;

import com.nexus.jobboard.domain.model.Skill;
import com.nexus.jobboard.domain.model.SkillCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Skill repository interface following ISP
 * - Contains only skill-specific operations
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    // Basic queries
    Optional<Skill> findByName(String name);
    
    boolean existsByName(String name);
    
    // Active skills
    List<Skill> findByIsActiveTrueOrderByName();
    
    Page<Skill> findByIsActive(Boolean isActive, Pageable pageable);
    
    // Category-based queries
    Page<Skill> findByCategory(SkillCategory category, Pageable pageable);
    
    Page<Skill> findByCategoryAndIsActive(SkillCategory category, Boolean isActive, Pageable pageable);
    
    List<Skill> findByCategoryAndIsActiveTrueOrderByName(SkillCategory category);
    
    // Search functionality
    @Query("SELECT s FROM Skill s WHERE s.isActive = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Skill> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Skills by name list (for bulk operations)
    List<Skill> findByNameInAndIsActiveTrue(List<String> names);
    
    // Popular skills (skills used in many jobs)
    @Query("SELECT s FROM Skill s WHERE s.isActive = true AND SIZE(s.jobs) > :minJobCount ORDER BY SIZE(s.jobs) DESC")
    Page<Skill> findPopularSkills(@Param("minJobCount") int minJobCount, Pageable pageable);
    
    @Query("SELECT s FROM Skill s WHERE s.isActive = true ORDER BY SIZE(s.jobs) DESC")
    Page<Skill> findSkillsOrderByJobCount(Pageable pageable);
    
    // Skills for specific job
    @Query("SELECT s FROM Skill s JOIN s.jobs j WHERE j.id = :jobId AND s.isActive = true")
    List<Skill> findByJobId(@Param("jobId") Long jobId);
    
    // Technical vs soft skills
    @Query("SELECT s FROM Skill s WHERE s.isActive = true AND s.category IN :technicalCategories")
    Page<Skill> findTechnicalSkills(@Param("technicalCategories") List<SkillCategory> technicalCategories, Pageable pageable);
    
    // Statistics
    @Query("SELECT COUNT(s) FROM Skill s WHERE s.isActive = true")
    Long countActiveSkills();
    
    @Query("SELECT COUNT(s) FROM Skill s WHERE s.category = :category AND s.isActive = true")
    Long countActiveSkillsByCategory(@Param("category") SkillCategory category);
}
