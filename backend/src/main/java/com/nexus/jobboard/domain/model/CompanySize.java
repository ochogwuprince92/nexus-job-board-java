package com.nexus.jobboard.domain.model;

/**
 * Company size enumeration following SRP
 * - Single responsibility: Define company sizes and their characteristics
 */
public enum CompanySize {
    STARTUP("Startup (1-10 employees)", 1, 10),
    SMALL("Small (11-50 employees)", 11, 50),
    MEDIUM("Medium (51-200 employees)", 51, 200),
    LARGE("Large (201-1000 employees)", 201, 1000),
    ENTERPRISE("Enterprise (1000+ employees)", 1000, Integer.MAX_VALUE);
    
    private final String description;
    private final int minEmployees;
    private final int maxEmployees;
    
    CompanySize(String description, int minEmployees, int maxEmployees) {
        this.description = description;
        this.minEmployees = minEmployees;
        this.maxEmployees = maxEmployees;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getMinEmployees() {
        return minEmployees;
    }
    
    public int getMaxEmployees() {
        return maxEmployees;
    }
    
    public boolean isInRange(int employeeCount) {
        return employeeCount >= minEmployees && 
               (maxEmployees == Integer.MAX_VALUE || employeeCount <= maxEmployees);
    }
    
    public static CompanySize fromEmployeeCount(int employeeCount) {
        for (CompanySize size : values()) {
            if (size.isInRange(employeeCount)) {
                return size;
            }
        }
        return STARTUP; // Default fallback
    }
}
