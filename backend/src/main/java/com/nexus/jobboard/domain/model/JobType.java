package com.nexus.jobboard.domain.model;

/**
 * Job type enumeration following SRP
 * - Single responsibility: Define job types and their characteristics
 */
public enum JobType {
    FULL_TIME("Full-time position", true),
    PART_TIME("Part-time position", false),
    CONTRACT("Contract-based work", false),
    FREELANCE("Freelance opportunity", false),
    INTERNSHIP("Internship program", false),
    TEMPORARY("Temporary assignment", false);
    
    private final String description;
    private final boolean isFullTime;
    
    JobType(String description, boolean isFullTime) {
        this.description = description;
        this.isFullTime = isFullTime;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFullTime() {
        return isFullTime;
    }
    
    public boolean requiresBenefits() {
        return isFullTime;
    }
}
