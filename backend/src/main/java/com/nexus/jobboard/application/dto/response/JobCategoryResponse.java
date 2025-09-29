package com.nexus.jobboard.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Job category response DTO following SRP
 * - Single responsibility: Present job category data to clients
 */
@Data
@Builder
public class JobCategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Boolean isActive;
    private Integer jobCount;
    private Integer activeJobCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
