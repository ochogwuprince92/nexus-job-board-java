package com.nexus.jobboard.application.dto.response;

import com.nexus.jobboard.domain.model.CompanySize;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Company response DTO following SRP
 * - Single responsibility: Present company data to clients
 */
@Data
@Builder
public class CompanyResponse {
    
    private Long id;
    private String name;
    private String description;
    private String website;
    private String industry;
    private String location;
    private String logoUrl;
    private CompanySize size;
    private Boolean isVerified;
    private Integer activeJobCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    public String getSizeDescription() {
        return size != null ? size.getDescription() : null;
    }
    
    public boolean canPostJobs() {
        return Boolean.TRUE.equals(isVerified);
    }
}
