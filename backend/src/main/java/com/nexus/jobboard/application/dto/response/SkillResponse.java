package com.nexus.jobboard.application.dto.response;

import com.nexus.jobboard.domain.model.SkillCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Skill response DTO following SRP
 * - Single responsibility: Present skill data to clients
 */
@Data
@Builder
public class SkillResponse {
    
    private Long id;
    private String name;
    private String description;
    private SkillCategory category;
    private Boolean isActive;
    private Integer jobCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    public String getCategoryDescription() {
        return category != null ? category.getDescription() : null;
    }
    
    public boolean isTechnicalSkill() {
        return category != null && category.isTechnical();
    }
}
