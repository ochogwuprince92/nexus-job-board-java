package com.nexus.jobboard.domain.model;

/**
 * Application status enumeration following SRP
 * - Single responsibility: Define application statuses and their behavior
 */
public enum ApplicationStatus {
    PENDING("Application submitted and pending review", false),
    REVIEWING("Application is being reviewed", false),
    SHORTLISTED("Candidate has been shortlisted", false),
    INTERVIEW_SCHEDULED("Interview has been scheduled", false),
    REJECTED("Application has been rejected", true),
    ACCEPTED("Application has been accepted", true),
    WITHDRAWN("Application has been withdrawn by candidate", true);
    
    private final String description;
    private final boolean isFinal;
    
    ApplicationStatus(String description, boolean isFinal) {
        this.description = description;
        this.isFinal = isFinal;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFinalStatus() {
        return isFinal;
    }
    
    public boolean canTransitionTo(ApplicationStatus newStatus) {
        if (this.isFinal) {
            return false; // Cannot change from final status
        }
        
        // Define valid transitions
        return switch (this) {
            case PENDING -> newStatus != PENDING;
            case REVIEWING -> newStatus != PENDING;
            case SHORTLISTED -> newStatus == INTERVIEW_SCHEDULED || 
                              newStatus == ACCEPTED || 
                              newStatus == REJECTED;
            case INTERVIEW_SCHEDULED -> newStatus == ACCEPTED || 
                                      newStatus == REJECTED;
            default -> false;
        };
    }
    
    public boolean isPositiveStatus() {
        return this == SHORTLISTED || 
               this == INTERVIEW_SCHEDULED || 
               this == ACCEPTED;
    }
}
