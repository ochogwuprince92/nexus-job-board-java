package com.nexus.jobboard.domain.model;

/**
 * Salary type enumeration following SRP
 * - Single responsibility: Define salary types and their calculation methods
 */
public enum SalaryType {
    HOURLY("Per hour", 1),
    DAILY("Per day", 8),
    WEEKLY("Per week", 40),
    MONTHLY("Per month", 160),
    YEARLY("Per year", 2080),
    PROJECT_BASED("Per project", 0);
    
    private final String description;
    private final int hoursPerPeriod;
    
    SalaryType(String description, int hoursPerPeriod) {
        this.description = description;
        this.hoursPerPeriod = hoursPerPeriod;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getHoursPerPeriod() {
        return hoursPerPeriod;
    }
    
    public boolean isTimeBasedSalary() {
        return hoursPerPeriod > 0;
    }
}
