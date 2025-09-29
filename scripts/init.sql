-- Database initialization script for Nexus Job Board
-- Following SOLID principles with proper database design

-- Create database if not exists (handled by Docker)
-- CREATE DATABASE IF NOT EXISTS nexus_job_board;

-- Enable UUID extension for PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create indexes for better performance
-- These will be created by JPA, but we can add custom ones here

-- Full-text search indexes
CREATE INDEX IF NOT EXISTS idx_job_search 
ON jobs USING gin(to_tsvector('english', title || ' ' || description));

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_job_active_created 
ON jobs(is_active, created_at DESC) WHERE is_active = true;

CREATE INDEX IF NOT EXISTS idx_job_location_type 
ON jobs(location, job_type) WHERE is_active = true;

CREATE INDEX IF NOT EXISTS idx_job_salary_range 
ON jobs(salary_min, salary_max) WHERE is_active = true AND salary_min IS NOT NULL;

-- Application status tracking index
CREATE INDEX IF NOT EXISTS idx_application_status_date 
ON job_applications(status, applied_at DESC);

-- User search optimization
CREATE INDEX IF NOT EXISTS idx_user_name_search 
ON users USING gin(to_tsvector('english', first_name || ' ' || last_name));

-- Company search optimization
CREATE INDEX IF NOT EXISTS idx_company_search 
ON companies USING gin(to_tsvector('english', name || ' ' || COALESCE(description, '')));

-- Insert default data
INSERT INTO job_categories (name, description, is_active, created_at, updated_at) VALUES
('Technology', 'Software development, IT, and tech roles', true, NOW(), NOW()),
('Marketing', 'Digital marketing, content, and advertising roles', true, NOW(), NOW()),
('Sales', 'Sales representatives, account managers, and business development', true, NOW(), NOW()),
('Design', 'UI/UX design, graphic design, and creative roles', true, NOW(), NOW()),
('Finance', 'Accounting, financial analysis, and banking roles', true, NOW(), NOW()),
('Healthcare', 'Medical, nursing, and healthcare administration roles', true, NOW(), NOW()),
('Education', 'Teaching, training, and educational roles', true, NOW(), NOW()),
('Operations', 'Operations management, logistics, and supply chain', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

INSERT INTO skills (name, description, category, is_active, created_at, updated_at) VALUES
-- Technical Skills
('Java', 'Java programming language', 'TECHNICAL', true, NOW(), NOW()),
('Spring Boot', 'Spring Boot framework for Java', 'FRAMEWORK', true, NOW(), NOW()),
('React', 'React.js frontend library', 'FRAMEWORK', true, NOW(), NOW()),
('PostgreSQL', 'PostgreSQL database management', 'TECHNICAL', true, NOW(), NOW()),
('Redis', 'Redis caching and data structure store', 'TOOL', true, NOW(), NOW()),
('Docker', 'Containerization platform', 'TOOL', true, NOW(), NOW()),
('Kubernetes', 'Container orchestration platform', 'TOOL', true, NOW(), NOW()),
('AWS', 'Amazon Web Services cloud platform', 'TOOL', true, NOW(), NOW()),
('Git', 'Version control system', 'TOOL', true, NOW(), NOW()),
('REST API', 'RESTful API design and development', 'TECHNICAL', true, NOW(), NOW()),

-- Soft Skills
('Communication', 'Effective verbal and written communication', 'SOFT_SKILLS', true, NOW(), NOW()),
('Leadership', 'Team leadership and management skills', 'SOFT_SKILLS', true, NOW(), NOW()),
('Problem Solving', 'Analytical and critical thinking skills', 'SOFT_SKILLS', true, NOW(), NOW()),
('Teamwork', 'Collaboration and team working skills', 'SOFT_SKILLS', true, NOW(), NOW()),
('Time Management', 'Efficient time and task management', 'SOFT_SKILLS', true, NOW(), NOW()),

-- Languages
('English', 'English language proficiency', 'LANGUAGE', true, NOW(), NOW()),
('Spanish', 'Spanish language proficiency', 'LANGUAGE', true, NOW(), NOW()),
('French', 'French language proficiency', 'LANGUAGE', true, NOW(), NOW()),

-- Certifications
('AWS Certified', 'AWS certification', 'CERTIFICATION', true, NOW(), NOW()),
('PMP', 'Project Management Professional certification', 'CERTIFICATION', true, NOW(), NOW()),
('Scrum Master', 'Certified Scrum Master', 'CERTIFICATION', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Create default admin user (password: admin123)
-- Note: In production, this should be done securely
INSERT INTO users (email, password, first_name, last_name, role, is_active, is_email_verified, created_at, updated_at) VALUES
('admin@nexusjobs.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System', 'Administrator', 'ADMIN', true, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Performance optimization: Update table statistics
ANALYZE users;
ANALYZE companies;
ANALYZE jobs;
ANALYZE job_applications;
ANALYZE job_categories;
ANALYZE skills;
