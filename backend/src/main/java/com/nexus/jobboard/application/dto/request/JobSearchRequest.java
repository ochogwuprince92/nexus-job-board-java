package com.nexus.jobboard.application.dto.request;

import com.nexus.jobboard.domain.model.JobType;
import com.nexus.jobboard.domain.model.ExperienceLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Job search request DTO following SRP
 * - Single responsibility: Handle job search criteria
 */
@Data
public class JobSearchRequest {
    
    private String searchTerm;
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Boolean isRemote;
    private Long categoryId;
    private List<Long> skillIds;
    private String companyName;
    
    /**
     * Check if any search criteria is provided
     */
    public boolean hasSearchCriteria() {
        return searchTerm != null || location != null || jobType != null ||
               experienceLevel != null || minSalary != null || maxSalary != null ||
               isRemote != null || categoryId != null || 
               (skillIds != null && !skillIds.isEmpty()) || companyName != null;
    }
    
    /**
     * Check if salary range is valid
     */
    public boolean isValidSalaryRange() {
        if (minSalary == null || maxSalary == null) {
            return true;
        }
        return minSalary.compareTo(maxSalary) <= 0;
    }
}
