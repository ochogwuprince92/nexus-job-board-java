package com.nexus.jobboard.presentation.controller;

import com.nexus.jobboard.application.dto.ai.JobRecommendationResult;
import com.nexus.jobboard.application.dto.ai.ResumeAnalysisResult;
import com.nexus.jobboard.application.dto.response.JobResponse;
import com.nexus.jobboard.application.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * AI controller following SRP
 * - Single responsibility: Handle AI-powered endpoints
 * - Depends on service abstractions (DIP)
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AI Features", description = "AI-powered job board features")
public class AIController {
    
    private final AIService aiService;
    
    @PostMapping("/resume/parse")
    @Operation(summary = "Parse resume", description = "Parse uploaded resume and extract structured information")
    @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
    public ResponseEntity<ResumeAnalysisResult> parseResume(@RequestParam("resume") MultipartFile resume) {
        log.info("Parsing resume file: {}", resume.getOriginalFilename());
        
        try {
            byte[] resumeContent = resume.getBytes();
            String contentType = resume.getContentType();
            
            ResumeAnalysisResult result = aiService.parseResume(resumeContent, contentType);
            log.info("Resume parsed successfully, found {} skills", result.getSkills().size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error parsing resume: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/recommendations")
    @Operation(summary = "Get job recommendations", description = "Get AI-powered job recommendations for the user")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Page<JobResponse>> getJobRecommendations(
            Authentication authentication,
            Pageable pageable) {
        Long userId = extractUserIdFromAuthentication(authentication);
        log.info("Getting job recommendations for user: {}", userId);
        
        Page<JobResponse> recommendations = aiService.getJobRecommendations(userId, pageable);
        log.info("Retrieved {} job recommendations for user: {}", recommendations.getTotalElements(), userId);
        
        return ResponseEntity.ok(recommendations);
    }
    
    @PostMapping("/recommendations/from-resume")
    @Operation(summary = "Get recommendations from resume", description = "Get job recommendations based on resume analysis")
    @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
    public ResponseEntity<List<JobRecommendationResult>> getRecommendationsFromResume(
            @RequestParam("resume") MultipartFile resume) {
        log.info("Getting recommendations from resume: {}", resume.getOriginalFilename());
        
        try {
            byte[] resumeContent = resume.getBytes();
            String contentType = resume.getContentType();
            
            // First parse the resume
            ResumeAnalysisResult resumeAnalysis = aiService.parseResume(resumeContent, contentType);
            
            // Then get recommendations
            List<JobRecommendationResult> recommendations = aiService.getRecommendationsFromResume(resumeAnalysis);
            log.info("Generated {} recommendations from resume", recommendations.size());
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error getting recommendations from resume: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/match-score")
    @Operation(summary = "Calculate job match score", description = "Calculate match score between user and job")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Double> calculateJobMatchScore(
            @RequestParam Long jobId,
            Authentication authentication) {
        Long userId = extractUserIdFromAuthentication(authentication);
        log.info("Calculating match score for user: {} and job: {}", userId, jobId);
        
        double matchScore = aiService.calculateJobMatchScore(userId, jobId);
        log.info("Match score calculated: {} for user: {} and job: {}", matchScore, userId, jobId);
        
        return ResponseEntity.ok(matchScore);
    }
    
    @PostMapping("/extract-skills")
    @Operation(summary = "Extract skills from job description", description = "Extract skills from job description text")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> extractSkillsFromJobDescription(
            @RequestBody String jobDescription) {
        log.info("Extracting skills from job description");
        
        List<String> skills = aiService.extractSkillsFromJobDescription(jobDescription);
        log.info("Extracted {} skills from job description", skills.size());
        
        return ResponseEntity.ok(skills);
    }
    
    @PostMapping("/job-description/suggestions")
    @Operation(summary = "Generate job description suggestions", description = "Generate job description suggestions based on title and industry")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public ResponseEntity<String> generateJobDescriptionSuggestions(
            @RequestParam String title,
            @RequestParam String industry,
            @RequestParam String level) {
        log.info("Generating job description suggestions for: {} in {}", title, industry);
        
        String suggestions = aiService.generateJobDescriptionSuggestions(title, industry, level);
        log.info("Generated job description suggestions for: {}", title);
        
        return ResponseEntity.ok(suggestions);
    }
    
    @GetMapping("/market-trends")
    @Operation(summary = "Analyze job market trends", description = "Get job market trends analysis")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> analyzeJobMarketTrends(
            @RequestParam String location,
            @RequestParam String industry) {
        log.info("Analyzing job market trends for {} in {}", industry, location);
        
        List<String> trends = aiService.analyzeJobMarketTrends(location, industry);
        log.info("Retrieved {} market trends for {} in {}", trends.size(), industry, location);
        
        return ResponseEntity.ok(trends);
    }
    
    // Helper method to extract user ID from authentication
    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - in real implementation, you'd extract the user ID
        // from the JWT token or UserDetails
        return 1L; // Placeholder
    }
}
