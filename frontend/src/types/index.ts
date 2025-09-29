/**
 * Type definitions following SOLID principles
 * - SRP: Each interface has single responsibility
 * - ISP: Segregated interfaces for different concerns
 */

// User types
export interface User {
  id: number;
  email: string;
  phoneNumber?: string;
  firstName: string;
  lastName: string;
  fullName: string;
  role: UserRole;
  isActive: boolean;
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  EMPLOYER = 'EMPLOYER',
  JOB_SEEKER = 'JOB_SEEKER'
}

// Job types
export interface Job {
  id: number;
  title: string;
  description: string;
  requirements?: string;
  company: Company;
  category?: JobCategory;
  jobType: JobType;
  experienceLevel: ExperienceLevel;
  location?: string;
  salaryMin?: number;
  salaryMax?: number;
  salaryType?: SalaryType;
  isRemote: boolean;
  isActive: boolean;
  applicationDeadline?: string;
  postedBy: User;
  requiredSkills: Skill[];
  applicationCount: number;
  createdAt: string;
  updatedAt: string;
}

export enum JobType {
  FULL_TIME = 'FULL_TIME',
  PART_TIME = 'PART_TIME',
  CONTRACT = 'CONTRACT',
  FREELANCE = 'FREELANCE',
  INTERNSHIP = 'INTERNSHIP',
  TEMPORARY = 'TEMPORARY'
}

export enum ExperienceLevel {
  ENTRY_LEVEL = 'ENTRY_LEVEL',
  MID_LEVEL = 'MID_LEVEL',
  SENIOR_LEVEL = 'SENIOR_LEVEL',
  EXECUTIVE = 'EXECUTIVE',
  INTERNSHIP = 'INTERNSHIP'
}

export enum SalaryType {
  HOURLY = 'HOURLY',
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  YEARLY = 'YEARLY',
  PROJECT_BASED = 'PROJECT_BASED'
}

// Company types
export interface Company {
  id: number;
  name: string;
  description?: string;
  website?: string;
  industry?: string;
  location?: string;
  logoUrl?: string;
  size?: CompanySize;
  isVerified: boolean;
  activeJobCount: number;
  createdAt: string;
  updatedAt: string;
}

export enum CompanySize {
  STARTUP = 'STARTUP',
  SMALL = 'SMALL',
  MEDIUM = 'MEDIUM',
  LARGE = 'LARGE',
  ENTERPRISE = 'ENTERPRISE'
}

// Job Category types
export interface JobCategory {
  id: number;
  name: string;
  description?: string;
  iconUrl?: string;
  isActive: boolean;
  jobCount: number;
  activeJobCount: number;
  createdAt: string;
  updatedAt: string;
}

// Skill types
export interface Skill {
  id: number;
  name: string;
  description?: string;
  category: SkillCategory;
  isActive: boolean;
  jobCount: number;
  createdAt: string;
  updatedAt: string;
}

export enum SkillCategory {
  TECHNICAL = 'TECHNICAL',
  SOFT_SKILLS = 'SOFT_SKILLS',
  LANGUAGE = 'LANGUAGE',
  CERTIFICATION = 'CERTIFICATION',
  TOOL = 'TOOL',
  FRAMEWORK = 'FRAMEWORK'
}

// Job Application types
export interface JobApplication {
  id: number;
  job: Job;
  applicant: User;
  coverLetter?: string;
  resumeUrl?: string;
  status: ApplicationStatus;
  notes?: string;
  appliedAt: string;
  reviewedAt?: string;
  reviewedBy?: User;
  portfolioUrl?: string;
  linkedInProfile?: string;
  githubProfile?: string;
  additionalNotes?: string;
  expectedSalary?: string;
  availabilityDate?: string;
}

export enum ApplicationStatus {
  PENDING = 'PENDING',
  REVIEWING = 'REVIEWING',
  SHORTLISTED = 'SHORTLISTED',
  INTERVIEW_SCHEDULED = 'INTERVIEW_SCHEDULED',
  REJECTED = 'REJECTED',
  ACCEPTED = 'ACCEPTED',
  WITHDRAWN = 'WITHDRAWN'
}

// Authentication types
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: string;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  email?: string;
  phoneNumber?: string;
  password: string;
  firstName: string;
  lastName: string;
  role: UserRole;
}

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Search and Filter types
export interface JobSearchFilters {
  query?: string;
  location?: string;
  jobType?: JobType;
  experienceLevel?: ExperienceLevel;
  minSalary?: number;
  maxSalary?: number;
  isRemote?: boolean;
  categoryId?: number;
  skillIds?: number[];
  companyName?: string;
}

// AI types
export interface ResumeAnalysisResult {
  personalInfo: PersonalInfo;
  workExperience: WorkExperience[];
  education: Education[];
  skills: string[];
  certifications: string[];
  languages: string[];
  summary?: string;
  skillConfidenceScores: Record<string, number>;
  totalExperienceYears: number;
  seniorityLevel: string;
  industries: string[];
  overallQualityScore: number;
}

export interface PersonalInfo {
  fullName?: string;
  email?: string;
  phone?: string;
  location?: string;
  linkedIn?: string;
  github?: string;
  portfolio?: string;
}

export interface WorkExperience {
  company: string;
  position: string;
  startDate: string;
  endDate?: string;
  description: string;
  achievements: string[];
  technologies: string[];
  isCurrent: boolean;
  durationMonths: number;
}

export interface Education {
  institution: string;
  degree: string;
  field: string;
  startDate: string;
  endDate?: string;
  gpa?: string;
  achievements: string[];
}

export interface JobRecommendationResult {
  job: Job;
  matchScore: number;
  matchReason: string;
  matchingSkills: string[];
  missingSkills: string[];
  insights: RecommendationInsights;
  confidenceLevel: number;
}

export interface RecommendationInsights {
  skillMatchScores: Record<string, number>;
  experienceMatch: number;
  locationMatch: number;
  salaryMatch: number;
  industryMatch: number;
  strengthAreas: string[];
  improvementAreas: string[];
  careerAdvice: string;
}
