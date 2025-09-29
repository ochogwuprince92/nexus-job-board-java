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
 * Company domain model following SRP
 * - Responsible only for company information and related business logic
 */
@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_company_name", columnList = "name"),
    @Index(name = "idx_company_industry", columnList = "industry"),
    @Index(name = "idx_company_verified", columnList = "isVerified")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String website;
    
    private String industry;
    
    private String location;
    
    private String logoUrl;
    
    @Enumerated(EnumType.STRING)
    private CompanySize size;
    
    @Builder.Default
    private Boolean isVerified = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    
    // Business logic methods following SRP
    public boolean isOwnedBy(User user) {
        return createdBy != null && createdBy.getId().equals(user.getId());
    }
    
    public int getActiveJobCount() {
        return jobs != null ? 
            (int) jobs.stream().filter(Job::getIsActive).count() : 0;
    }
    
    public boolean canPostJobs() {
        return isVerified;
    }
    
    public void verify() {
        this.isVerified = true;
    }
    
    public void unverify() {
        this.isVerified = false;
    }
}
