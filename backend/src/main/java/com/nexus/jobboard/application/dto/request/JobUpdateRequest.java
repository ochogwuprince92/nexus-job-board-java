package com.nexus.jobboard.application.dto.request;

import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
import com.nexus.jobboard.domain.model.SalaryType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Job update request DTO following SRP
 * - Single responsibility: Handle job update data validation
 */
@Data
public class JobUpdateRequest {
    
    @Size(max = 200, message = "Job title must not exceed 200 characters")
    private String title;
    
    @Size(max = 5000, message = "Job description must not exceed 5000 characters")
    private String description;
    
    @Size(max = 3000, message = "Job requirements must not exceed 3000 characters")
    private String requirements;
    
    private Long categoryId;
    
    private JobType jobType;
    
    private ExperienceLevel experienceLevel;
    
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
    
    @Positive(message = "Minimum salary must be positive")
    private BigDecimal salaryMin;
    
    @Positive(message = "Maximum salary must be positive")
    private BigDecimal salaryMax;
    
    private SalaryType salaryType;
    
    private Boolean isRemote;
    
    private LocalDateTime applicationDeadline;
    
    private List<Long> requiredSkillIds;
    
    /**
     * Check if any field is provided for update
     */
    public boolean hasUpdates() {
        return title != null || description != null || requirements != null ||
               categoryId != null || jobType != null || experienceLevel != null ||
               location != null || salaryMin != null || salaryMax != null ||
               salaryType != null || isRemote != null || applicationDeadline != null ||
               requiredSkillIds != null;
    }
    
    /**
     * Validate salary range if both values are provided
     */
    public boolean isValidSalaryRange() {
        if (salaryMin == null || salaryMax == null) {
            return true;
        }
        return salaryMin.compareTo(salaryMax) <= 0;
    }
}
