package com.nexus.jobboard.domain.model;

/**
 * User roles enum following SRP
 * - Single responsibility: Define user roles and their descriptions
 */
public enum UserRole {
    ADMIN("Administrator with full system access"),
    EMPLOYER("Company representative who can post and manage jobs"),
    JOB_SEEKER("Individual looking for job opportunities");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    public boolean isEmployer() {
        return this == EMPLOYER;
    }
    
    public boolean isJobSeeker() {
        return this == JOB_SEEKER;
    }
}
