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
 * Skill domain model following SRP
 * - Responsible only for skill information and categorization
 */
@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skill_name", columnList = "name"),
    @Index(name = "idx_skill_category", columnList = "category"),
    @Index(name = "idx_skill_active", columnList = "isActive")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private SkillCategory category;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @ManyToMany(mappedBy = "requiredSkills", fetch = FetchType.LAZY)
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
    
    public boolean isTechnicalSkill() {
        return category == SkillCategory.TECHNICAL || 
               category == SkillCategory.TOOL || 
               category == SkillCategory.FRAMEWORK;
    }
    
    public boolean isSoftSkill() {
        return category == SkillCategory.SOFT_SKILLS;
    }
}
