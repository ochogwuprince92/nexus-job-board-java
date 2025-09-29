package com.nexus.jobboard.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Job application domain model following SRP
 * - Responsible only for application process and status management
 */
@Entity
@Table(name = "job_applications", indexes = {
    @Index(name = "idx_application_job", columnList = "job_id"),
    @Index(name = "idx_application_applicant", columnList = "applicant_id"),
    @Index(name = "idx_application_status", columnList = "status"),
    @Index(name = "idx_application_date", columnList = "appliedAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    @Column(columnDefinition = "TEXT")
    private String coverLetter;
    
    private String resumeUrl;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;
    
    private LocalDateTime reviewedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
    
    // Business logic methods following SRP
    public void updateStatus(ApplicationStatus newStatus, User reviewer) {
        this.status = newStatus;
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public boolean isOwnedBy(User user) {
        return applicant != null && applicant.getId().equals(user.getId());
    }
    
    public boolean canBeReviewedBy(User user) {
        return job != null && 
               (job.isOwnedBy(user) || user.hasRole(UserRole.ADMIN));
    }
    
    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }
    
    public boolean isFinalStatus() {
        return status != null && status.isFinalStatus();
    }
    
    public void withdraw() {
        if (!isFinalStatus()) {
            this.status = ApplicationStatus.WITHDRAWN;
            this.reviewedAt = LocalDateTime.now();
        }
    }
}
