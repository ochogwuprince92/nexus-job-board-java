package com.nexus.jobboard.application.dto.ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Resume analysis result DTO following SRP
 * - Single responsibility: Carry resume analysis data
 */
@Data
@Builder
public class ResumeAnalysisResult {
    
    private PersonalInfo personalInfo;
    private List<WorkExperience> workExperience;
    private List<Education> education;
    private List<String> skills;
    private List<String> certifications;
    private List<String> languages;
    private String summary;
    private Map<String, Double> skillConfidenceScores;
    private int totalExperienceYears;
    private String seniorityLevel;
    private List<String> industries;
    private double overallQualityScore;
    
    @Data
    @Builder
    public static class PersonalInfo {
        private String fullName;
        private String email;
        private String phone;
        private String location;
        private String linkedIn;
        private String github;
        private String portfolio;
    }
    
    @Data
    @Builder
    public static class WorkExperience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
        private List<String> achievements;
        private List<String> technologies;
        private boolean isCurrent;
        private int durationMonths;
    }
    
    @Data
    @Builder
    public static class Education {
        private String institution;
        private String degree;
        private String field;
        private String startDate;
        private String endDate;
        private String gpa;
        private List<String> achievements;
    }
}
