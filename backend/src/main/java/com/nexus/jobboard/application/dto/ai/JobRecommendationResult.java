package com.nexus.jobboard.application.dto.ai;

import com.nexus.jobboard.application.dto.response.JobResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Job recommendation result DTO following SRP
 * - Single responsibility: Carry job recommendation data with AI insights
 */
@Data
@Builder
public class JobRecommendationResult {
    
    private JobResponse job;
    private double matchScore;
    private String matchReason;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private RecommendationInsights insights;
    private int confidenceLevel; // 1-100
    
    @Data
    @Builder
    public static class RecommendationInsights {
        private Map<String, Double> skillMatchScores;
        private double experienceMatch;
        private double locationMatch;
        private double salaryMatch;
        private double industryMatch;
        private List<String> strengthAreas;
        private List<String> improvementAreas;
        private String careerAdvice;
    }
    
    public boolean isHighConfidenceMatch() {
        return confidenceLevel >= 80 && matchScore >= 0.7;
    }
    
    public boolean isGoodMatch() {
        return confidenceLevel >= 60 && matchScore >= 0.5;
    }
    
    public String getMatchGrade() {
        if (matchScore >= 0.9) return "Excellent";
        if (matchScore >= 0.7) return "Very Good";
        if (matchScore >= 0.5) return "Good";
        if (matchScore >= 0.3) return "Fair";
        return "Poor";
    }
}
