package com.nexus.jobboard.application.service.impl;

import com.nexus.jobboard.application.dto.ai.JobRecommendationResult;
import com.nexus.jobboard.application.dto.ai.ResumeAnalysisResult;
import com.nexus.jobboard.application.dto.response.JobResponse;
import com.nexus.jobboard.application.mapper.JobMapper;
import com.nexus.jobboard.application.service.AIService;
import com.nexus.jobboard.domain.model.Job;
import com.nexus.jobboard.domain.model.User;
import com.nexus.jobboard.domain.repository.JobRepository;
import com.nexus.jobboard.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * AI service implementation following SOLID principles
 * 
 * SRP: Handles only AI-related operations
 * OCP: Open for extension with different AI providers
 * LSP: Substitutable for AIService interface
 * ISP: Implements specific AI methods
 * DIP: Depends on abstractions (repositories, mappers)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {
    
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final Tika tika = new Tika();
    
    // Common skills patterns for extraction
    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "Java", "Python", "JavaScript", "React", "Angular", "Vue", "Spring Boot", "Node.js",
        "Docker", "Kubernetes", "AWS", "Azure", "GCP", "PostgreSQL", "MySQL", "MongoDB",
        "Redis", "Git", "Jenkins", "CI/CD", "Agile", "Scrum", "REST API", "GraphQL",
        "Machine Learning", "Data Science", "AI", "TensorFlow", "PyTorch", "Pandas",
        "HTML", "CSS", "TypeScript", "C++", "C#", ".NET", "PHP", "Ruby", "Go", "Rust"
    );
    
    @Override
    public ResumeAnalysisResult parseResume(byte[] resumeContent, String contentType) {
        log.info("Parsing resume with content type: {}", contentType);
        
        try {
            String text = tika.parseToString(new ByteArrayInputStream(resumeContent));
            return analyzeResumeText(text);
        } catch (Exception e) {
            log.error("Error parsing resume: {}", e.getMessage());
            throw new RuntimeException("Failed to parse resume", e);
        }
    }
    
    @Override
    public Page<JobResponse> getJobRecommendations(Long userId, Pageable pageable) {
        log.info("Getting job recommendations for user: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Page.empty();
        }
        
        // Get all active jobs
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(Job::getIsActive)
                .collect(Collectors.toList());
        
        // Calculate match scores and sort
        List<JobRecommendationResult> recommendations = allJobs.stream()
                .map(job -> calculateJobRecommendation(userOpt.get(), job))
                .filter(rec -> rec.getMatchScore() > 0.3) // Filter low matches
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());
        
        // Convert to JobResponse and paginate
        List<JobResponse> jobResponses = recommendations.stream()
                .map(rec -> rec.getJob())
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        
        return new PageImpl<>(jobResponses, pageable, recommendations.size());
    }
    
    @Override
    public List<JobRecommendationResult> getRecommendationsFromResume(ResumeAnalysisResult resumeAnalysis) {
        log.info("Getting recommendations from resume analysis");
        
        List<Job> allJobs = jobRepository.findAll().stream()
                .filter(Job::getIsActive)
                .collect(Collectors.toList());
        
        return allJobs.stream()
                .map(job -> calculateJobRecommendationFromResume(resumeAnalysis, job))
                .filter(rec -> rec.getMatchScore() > 0.4)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    @Override
    public double calculateJobMatchScore(Long userId, Long jobId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        
        if (userOpt.isEmpty() || jobOpt.isEmpty()) {
            return 0.0;
        }
        
        return calculateJobRecommendation(userOpt.get(), jobOpt.get()).getMatchScore();
    }
    
    @Override
    public List<String> extractSkillsFromJobDescription(String jobDescription) {
        log.info("Extracting skills from job description");
        
        String upperDescription = jobDescription.toUpperCase();
        
        return COMMON_SKILLS.stream()
                .filter(skill -> upperDescription.contains(skill.toUpperCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public String generateJobDescriptionSuggestions(String title, String industry, String level) {
        log.info("Generating job description suggestions for: {} in {}", title, industry);
        
        // This is a simplified implementation
        // In production, you'd use a more sophisticated AI model
        StringBuilder suggestions = new StringBuilder();
        
        suggestions.append("Job Description Suggestions for ").append(title).append(":\n\n");
        suggestions.append("Key Responsibilities:\n");
        suggestions.append("• Lead and manage ").append(title.toLowerCase()).append(" projects\n");
        suggestions.append("• Collaborate with cross-functional teams\n");
        suggestions.append("• Implement best practices and industry standards\n\n");
        
        suggestions.append("Required Skills:\n");
        getSkillsForRole(title).forEach(skill -> 
            suggestions.append("• ").append(skill).append("\n"));
        
        suggestions.append("\nExperience Level: ").append(level).append("\n");
        suggestions.append("Industry: ").append(industry);
        
        return suggestions.toString();
    }
    
    @Override
    public List<String> analyzeJobMarketTrends(String location, String industry) {
        log.info("Analyzing job market trends for {} in {}", industry, location);
        
        // Simplified implementation - in production, use real market data
        List<String> trends = new ArrayList<>();
        trends.add("Remote work opportunities increasing by 25%");
        trends.add("High demand for " + industry + " professionals");
        trends.add("Average salary growth of 8% in " + location);
        trends.add("Top skills in demand: AI, Cloud Computing, Data Science");
        trends.add("Startup ecosystem growing rapidly");
        
        return trends;
    }
    
    private ResumeAnalysisResult analyzeResumeText(String text) {
        log.debug("Analyzing resume text of length: {}", text.length());
        
        // Extract personal information
        ResumeAnalysisResult.PersonalInfo personalInfo = extractPersonalInfo(text);
        
        // Extract skills
        List<String> skills = extractSkills(text);
        
        // Calculate experience years
        int experienceYears = calculateExperienceYears(text);
        
        // Determine seniority level
        String seniorityLevel = determineSeniorityLevel(experienceYears, skills);
        
        // Extract work experience (simplified)
        List<ResumeAnalysisResult.WorkExperience> workExperience = extractWorkExperience(text);
        
        // Extract education (simplified)
        List<ResumeAnalysisResult.Education> education = extractEducation(text);
        
        return ResumeAnalysisResult.builder()
                .personalInfo(personalInfo)
                .workExperience(workExperience)
                .education(education)
                .skills(skills)
                .totalExperienceYears(experienceYears)
                .seniorityLevel(seniorityLevel)
                .overallQualityScore(calculateQualityScore(text, skills, experienceYears))
                .build();
    }
    
    private ResumeAnalysisResult.PersonalInfo extractPersonalInfo(String text) {
        // Email extraction
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher emailMatcher = emailPattern.matcher(text);
        String email = emailMatcher.find() ? emailMatcher.group() : null;
        
        // Phone extraction
        Pattern phonePattern = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b");
        Matcher phoneMatcher = phonePattern.matcher(text);
        String phone = phoneMatcher.find() ? phoneMatcher.group() : null;
        
        return ResumeAnalysisResult.PersonalInfo.builder()
                .email(email)
                .phone(phone)
                .build();
    }
    
    private List<String> extractSkills(String text) {
        String upperText = text.toUpperCase();
        
        return COMMON_SKILLS.stream()
                .filter(skill -> upperText.contains(skill.toUpperCase()))
                .collect(Collectors.toList());
    }
    
    private int calculateExperienceYears(String text) {
        // Simplified calculation based on year mentions
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = yearPattern.matcher(text);
        
        Set<Integer> years = new HashSet<>();
        while (matcher.find()) {
            years.add(Integer.parseInt(matcher.group()));
        }
        
        if (years.size() >= 2) {
            return Collections.max(years) - Collections.min(years);
        }
        
        return 0;
    }
    
    private String determineSeniorityLevel(int experienceYears, List<String> skills) {
        if (experienceYears >= 8 || skills.size() >= 15) {
            return "Senior";
        } else if (experienceYears >= 3 || skills.size() >= 8) {
            return "Mid-level";
        } else {
            return "Junior";
        }
    }
    
    private List<ResumeAnalysisResult.WorkExperience> extractWorkExperience(String text) {
        // Simplified work experience extraction
        List<ResumeAnalysisResult.WorkExperience> experiences = new ArrayList<>();
        
        // This would be more sophisticated in production
        if (text.toLowerCase().contains("software engineer")) {
            experiences.add(ResumeAnalysisResult.WorkExperience.builder()
                    .position("Software Engineer")
                    .company("Previous Company")
                    .description("Software development experience")
                    .build());
        }
        
        return experiences;
    }
    
    private List<ResumeAnalysisResult.Education> extractEducation(String text) {
        // Simplified education extraction
        List<ResumeAnalysisResult.Education> educationList = new ArrayList<>();
        
        if (text.toLowerCase().contains("bachelor") || text.toLowerCase().contains("b.s.") || text.toLowerCase().contains("b.a.")) {
            educationList.add(ResumeAnalysisResult.Education.builder()
                    .degree("Bachelor's Degree")
                    .field("Computer Science")
                    .build());
        }
        
        return educationList;
    }
    
    private double calculateQualityScore(String text, List<String> skills, int experienceYears) {
        double score = 0.0;
        
        // Base score from text length
        score += Math.min(text.length() / 1000.0 * 20, 30);
        
        // Skills contribution
        score += Math.min(skills.size() * 3, 40);
        
        // Experience contribution
        score += Math.min(experienceYears * 2, 30);
        
        return Math.min(score, 100.0);
    }
    
    private JobRecommendationResult calculateJobRecommendation(User user, Job job) {
        double matchScore = 0.0;
        List<String> matchingSkills = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        
        // Role-based matching
        if (user.getRole().name().equals("JOB_SEEKER")) {
            matchScore += 0.2;
            reasons.add("User is actively seeking jobs");
        }
        
        // Simple skill matching (in production, this would be more sophisticated)
        if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
            matchScore += 0.3;
            reasons.add("Job has defined skill requirements");
        }
        
        // Location matching (simplified)
        if (job.getIsRemote()) {
            matchScore += 0.2;
            reasons.add("Remote work opportunity");
        }
        
        // Experience level matching (simplified)
        matchScore += 0.3; // Base match
        
        JobResponse jobResponse = jobMapper.toResponse(job);
        
        return JobRecommendationResult.builder()
                .job(jobResponse)
                .matchScore(Math.min(matchScore, 1.0))
                .matchReason(String.join(", ", reasons))
                .matchingSkills(matchingSkills)
                .confidenceLevel((int) (matchScore * 100))
                .build();
    }
    
    private JobRecommendationResult calculateJobRecommendationFromResume(ResumeAnalysisResult resume, Job job) {
        double matchScore = 0.0;
        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        
        // Skill matching
        if (job.getRequiredSkills() != null && resume.getSkills() != null) {
            List<String> jobSkills = job.getRequiredSkills().stream()
                    .map(skill -> skill.getName())
                    .collect(Collectors.toList());
            
            for (String jobSkill : jobSkills) {
                if (resume.getSkills().contains(jobSkill)) {
                    matchingSkills.add(jobSkill);
                } else {
                    missingSkills.add(jobSkill);
                }
            }
            
            if (!jobSkills.isEmpty()) {
                matchScore += (double) matchingSkills.size() / jobSkills.size() * 0.6;
            }
        }
        
        // Experience level matching
        if (resume.getSeniorityLevel() != null && job.getExperienceLevel() != null) {
            if (resume.getSeniorityLevel().toLowerCase().contains(job.getExperienceLevel().name().toLowerCase())) {
                matchScore += 0.3;
            }
        }
        
        // Base score
        matchScore += 0.1;
        
        JobResponse jobResponse = jobMapper.toResponse(job);
        
        return JobRecommendationResult.builder()
                .job(jobResponse)
                .matchScore(Math.min(matchScore, 1.0))
                .matchingSkills(matchingSkills)
                .missingSkills(missingSkills)
                .confidenceLevel((int) (matchScore * 85)) // Slightly lower confidence for resume-based
                .build();
    }
    
    private List<String> getSkillsForRole(String title) {
        String lowerTitle = title.toLowerCase();
        
        if (lowerTitle.contains("java") || lowerTitle.contains("backend")) {
            return Arrays.asList("Java", "Spring Boot", "PostgreSQL", "REST API", "Microservices");
        } else if (lowerTitle.contains("frontend") || lowerTitle.contains("react")) {
            return Arrays.asList("JavaScript", "React", "HTML", "CSS", "TypeScript");
        } else if (lowerTitle.contains("data") || lowerTitle.contains("analyst")) {
            return Arrays.asList("Python", "SQL", "Pandas", "Machine Learning", "Data Visualization");
        } else {
            return Arrays.asList("Communication", "Problem Solving", "Teamwork", "Leadership");
        }
    }
}
