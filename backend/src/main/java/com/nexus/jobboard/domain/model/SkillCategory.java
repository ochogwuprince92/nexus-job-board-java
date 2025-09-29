package com.nexus.jobboard.domain.model;

/**
 * Skill category enumeration following SRP
 * - Single responsibility: Define skill categories and their characteristics
 */
public enum SkillCategory {
    TECHNICAL("Technical skills", true),
    SOFT_SKILLS("Soft skills", false),
    LANGUAGE("Language skills", false),
    CERTIFICATION("Professional certifications", true),
    TOOL("Tools and software", true),
    FRAMEWORK("Frameworks and libraries", true);
    
    private final String description;
    private final boolean isTechnical;
    
    SkillCategory(String description, boolean isTechnical) {
        this.description = description;
        this.isTechnical = isTechnical;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTechnical() {
        return isTechnical;
    }
    
    public boolean requiresVerification() {
        return this == CERTIFICATION;
    }
}
