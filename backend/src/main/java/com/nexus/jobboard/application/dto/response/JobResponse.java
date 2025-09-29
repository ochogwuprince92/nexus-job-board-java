package com.nexus.jobboard.application.dto.response;

import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
import com.nexus.jobboard.domain.model.SalaryType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Job response DTO following SRP
 * - Single responsibility: Present job data to clients
 */
@Data
@Builder
public class JobResponse {
    
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private CompanyResponse company;
    private JobCategoryResponse category;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private SalaryType salaryType;
    private Boolean isRemote;
    private Boolean isActive;
    private LocalDateTime applicationDeadline;
    private UserResponse postedBy;
    private List<SkillResponse> requiredSkills;
    private Integer applicationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    public String getJobTypeDescription() {
        return jobType != null ? jobType.getDescription() : null;
    }
    
    public String getExperienceLevelDescription() {
        return experienceLevel != null ? experienceLevel.getDescription() : null;
    }
    
    public String getSalaryTypeDescription() {
        return salaryType != null ? salaryType.getDescription() : null;
    }
    
    public boolean isApplicationDeadlinePassed() {
        return applicationDeadline != null && LocalDateTime.now().isAfter(applicationDeadline);
    }
    
    public boolean canAcceptApplications() {
        return Boolean.TRUE.equals(isActive) && !isApplicationDeadlinePassed();
    }
    
    public String getSalaryRange() {
        if (salaryMin == null && salaryMax == null) {
            return "Not specified";
        }
        if (salaryMin == null) {
            return "Up to " + salaryMax;
        }
        if (salaryMax == null) {
            return "From " + salaryMin;
        }
        return salaryMin + " - " + salaryMax;
    }
}
