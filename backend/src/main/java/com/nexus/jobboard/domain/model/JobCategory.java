package com.nexus.jobboard.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Job category domain model following SRP
 * - Responsible only for job categorization
 */
@Entity
@Table(name = "job_categories", indexes = {
    @Index(name = "idx_category_name", columnList = "name"),
    @Index(name = "idx_category_active", columnList = "isActive")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    private String iconUrl;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business logic methods
    public int getJobCount() {
        return jobs != null ? jobs.size() : 0;
    }
    
    public int getActiveJobCount() {
        return jobs != null ? 
            (int) jobs.stream().filter(Job::getIsActive).count() : 0;
    }
}
