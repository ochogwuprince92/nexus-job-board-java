package com.nexus.jobboard.domain.model;

/**
 * Experience level enumeration following SRP
 * - Single responsibility: Define experience levels and their requirements
 */
public enum ExperienceLevel {
    ENTRY_LEVEL("Entry level (0-2 years)", 0, 2),
    MID_LEVEL("Mid level (2-5 years)", 2, 5),
    SENIOR_LEVEL("Senior level (5-10 years)", 5, 10),
    EXECUTIVE("Executive level (10+ years)", 10, Integer.MAX_VALUE),
    INTERNSHIP("Internship/Student level", 0, 0);
    
    private final String description;
    private final int minYears;
    private final int maxYears;
    
    ExperienceLevel(String description, int minYears, int maxYears) {
        this.description = description;
        this.minYears = minYears;
        this.maxYears = maxYears;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getMinYears() {
        return minYears;
    }
    
    public int getMaxYears() {
        return maxYears;
    }
    
    public boolean isQualified(int yearsOfExperience) {
        return yearsOfExperience >= minYears && 
               (maxYears == Integer.MAX_VALUE || yearsOfExperience <= maxYears);
    }
}
