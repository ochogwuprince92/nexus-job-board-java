package com.nexus.jobboard.application.service;

import com.nexus.jobboard.application.dto.response.JobResponse;
import com.nexus.jobboard.application.dto.ai.ResumeAnalysisResult;
import com.nexus.jobboard.application.dto.ai.JobRecommendationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * AI service interface following DIP and SRP
 * - Single responsibility: AI-powered features
 * - Abstraction for different AI implementations
 */
public interface AIService {
    
    /**
     * Parse resume and extract structured information
     */
    ResumeAnalysisResult parseResume(byte[] resumeContent, String contentType);
    
    /**
     * Get job recommendations for a user based on their profile and preferences
     */
    Page<JobResponse> getJobRecommendations(Long userId, Pageable pageable);
    
    /**
     * Get job recommendations based on resume analysis
     */
    List<JobRecommendationResult> getRecommendationsFromResume(ResumeAnalysisResult resumeAnalysis);
    
    /**
     * Calculate job match score for a user and job
     */
    double calculateJobMatchScore(Long userId, Long jobId);
    
    /**
     * Extract skills from job description
     */
    List<String> extractSkillsFromJobDescription(String jobDescription);
    
    /**
     * Generate job description suggestions
     */
    String generateJobDescriptionSuggestions(String title, String industry, String level);
    
    /**
     * Analyze job market trends
     */
    List<String> analyzeJobMarketTrends(String location, String industry);
}
