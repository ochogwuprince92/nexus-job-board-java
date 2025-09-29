package com.nexus.jobboard.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Job domain model following SRP
 * - Responsible only for job posting information and related business logic
 */
@Entity
@Table(name = "jobs", indexes = {
    @Index(name = "idx_job_title", columnList = "title"),
    @Index(name = "idx_job_location", columnList = "location"),
    @Index(name = "idx_job_type", columnList = "jobType"),
    @Index(name = "idx_job_active", columnList = "isActive"),
    @Index(name = "idx_job_created", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private JobCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;
    
    private String location;
    
    private BigDecimal salaryMin;
    
    private BigDecimal salaryMax;
    
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
    
    @Builder.Default
    private Boolean isRemote = false;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private LocalDateTime applicationDeadline;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<JobApplication> applications = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private List<Skill> requiredSkills = new ArrayList<>();
    
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
    public boolean isApplicationDeadlinePassed() {
        return applicationDeadline != null && LocalDateTime.now().isAfter(applicationDeadline);
    }
    
    public boolean canAcceptApplications() {
        return isActive && !isApplicationDeadlinePassed();
    }
    
    public boolean isOwnedBy(User user) {
        return postedBy != null && postedBy.getId().equals(user.getId());
    }
    
    public boolean isOwnedByCompany(Company company) {
        return this.company != null && this.company.getId().equals(company.getId());
    }
    
    public int getApplicationCount() {
        return applications != null ? applications.size() : 0;
    }
    
    public void addSkill(Skill skill) {
        if (requiredSkills == null) {
            requiredSkills = new ArrayList<>();
        }
        if (!requiredSkills.contains(skill)) {
            requiredSkills.add(skill);
        }
    }
    
    public void removeSkill(Skill skill) {
        if (requiredSkills != null) {
            requiredSkills.remove(skill);
        }
    }
}
