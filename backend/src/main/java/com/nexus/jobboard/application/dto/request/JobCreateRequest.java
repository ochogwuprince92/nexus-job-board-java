package com.nexus.jobboard.application.dto.request;

import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
import com.nexus.jobboard.domain.model.SalaryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Job creation request DTO following SRP
 * - Single responsibility: Handle job creation data validation
 */
@Data
public class JobCreateRequest {
    
    @NotBlank(message = "Job title is required")
    @Size(max = 200, message = "Job title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Job description is required")
    @Size(max = 5000, message = "Job description must not exceed 5000 characters")
    private String description;
    
    @Size(max = 3000, message = "Job requirements must not exceed 3000 characters")
    private String requirements;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    private Long categoryId;
    
    @NotNull(message = "Job type is required")
    private JobType jobType;
    
    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;
    
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    @Positive(message = "Minimum salary must be positive")
    private BigDecimal salaryMin;
    
    @Positive(message = "Maximum salary must be positive")
    private BigDecimal salaryMax;
    
    private SalaryType salaryType;
    
    private Boolean isRemote = false;
    
    private LocalDateTime applicationDeadline;
    
    private List<Long> requiredSkillIds;
    
    /**
     * Validate salary range
     */
    public boolean isValidSalaryRange() {
        if (salaryMin == null || salaryMax == null) {
            return true; // Optional fields
        }
        return salaryMin.compareTo(salaryMax) <= 0;
    }
    
    /**
     * Check if application deadline is in the future
     */
    public boolean isValidApplicationDeadline() {
        return applicationDeadline == null || applicationDeadline.isAfter(LocalDateTime.now());
    }
}
