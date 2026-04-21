-- =============================================
-- HireConnect Dummy Data Seeder
-- Run this script to populate the database with sample data
-- =============================================

-- Clear existing data (optional - uncomment if needed)
-- DELETE FROM hireconnect_applications.applications WHERE 1=1;
-- DELETE FROM hireconnect_interviews.interviews WHERE 1=1;
-- DELETE FROM hireconnect_jobs.jobs WHERE 1=1;
-- DELETE FROM hireconnect_profile.profiles WHERE 1=1;
-- DELETE FROM hireconnect_notifications.notifications WHERE 1=1;
-- DELETE FROM hireconnect_auth.user_credentials WHERE 1=1;

-- =============================================
-- USERS
-- =============================================

USE hireconnect_auth;

INSERT INTO user_credentials (id, email, password, role, provider, created_at, last_login_at, active) VALUES
-- Candidates
(UUID_TO_BIN(UUID()), 'john.dev@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'CANDIDATE', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'sarah.tech@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'CANDIDATE', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'mike.code@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'CANDIDATE', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'emma.fullstack@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'CANDIDATE', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'alex.data@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'CANDIDATE', 'LOCAL', NOW(), NOW(), TRUE),

-- Recruiters
(UUID_TO_BIN(UUID()), 'hr@google.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'careers@microsoft.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'talent@amazon.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'jobs@meta.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'hiring@netflix.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'recruit@apple.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE),
(UUID_TO_BIN(UUID()), 'careers@spotify.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrXqgZ2tV0z8RhtH5GJ5GJ5GJ5GJ5G', 'RECRUITER', 'LOCAL', NOW(), NOW(), TRUE);

-- =============================================
-- PROFILES
-- =============================================

USE hireconnect_profile;

INSERT INTO profiles (user_id, full_name, email, mobile, headline, bio, location, skills, experience_level, preferred_job_types, expected_salary_min, expected_salary_max, created_at, updated_at, is_candidate) VALUES
-- Candidate Profiles
(UNHEX(REPLACE(UUID(), '-', '')), 'John Developer', 'john.dev@email.com', '9876543210', 'Full Stack Developer | 5+ Years Experience', 'Passionate about building scalable web applications. Expert in Java, Spring Boot, and React.', 'Bangalore, India', 'Java,Spring Boot,React,MySQL,Docker', 'SENIOR', 'FULL_TIME,REMOTE', 1500000, 3000000, NOW(), NOW(), TRUE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Sarah Tech', 'sarah.tech@email.com', '9876543211', 'Frontend Specialist | React & Vue Expert', 'Creative frontend developer focused on building beautiful user interfaces.', 'Hyderabad, India', 'JavaScript,React,Vue.js,HTML,CSS', 'MID_LEVEL', 'FULL_TIME', 1200000, 2500000, NOW(), NOW(), TRUE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Mike Coder', 'mike.code@email.com', '9876543212', 'Backend Engineer | Java & Python', 'Experienced backend developer with strong algorithmic skills.', 'Chennai, India', 'Java,Python,PostgreSQL,Kafka,MongoDB', 'SENIOR', 'FULL_TIME,HYBRID', 1800000, 3500000, NOW(), NOW(), TRUE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Emma Fullstack', 'emma.fullstack@email.com', '9876543213', 'Full Stack Developer | MERN Stack', 'Versatile developer comfortable with both frontend and backend technologies.', 'Pune, India', 'Node.js,React,MongoDB,Express,GraphQL', 'MID_LEVEL', 'FULL_TIME,REMOTE', 1400000, 2800000, NOW(), NOW(), TRUE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Alex Data Scientist', 'alex.data@email.com', '9876543214', 'Data Scientist | ML & AI', 'Data scientist with expertise in machine learning and predictive modeling.', 'Mumbai, India', 'Python,TensorFlow,Scikit-learn,SQL,Statistics', 'SENIOR', 'FULL_TIME', 2000000, 4000000, NOW(), NOW(), TRUE),

-- Recruiter Profiles
(UNHEX(REPLACE(UUID(), '-', '')), 'Google HR', 'hr@google.com', '9000000001', 'Technical Recruiter at Google', 'Looking for exceptional engineers to join Google.', 'Bangalore, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Microsoft Careers', 'careers@microsoft.com', '9000000002', 'Senior Recruiter at Microsoft', 'Building the future with talented individuals at Microsoft.', 'Hyderabad, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Amazon Talent', 'talent@amazon.com', '9000000003', 'Talent Acquisition at Amazon', 'Hiring the best minds for Amazon innovation.', 'Bangalore, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Meta Recruiting', 'jobs@meta.com', '9000000004', 'Recruiting Lead at Meta', 'Connecting top talent with Meta mission-critical roles.', 'Mumbai, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Netflix Hiring', 'hiring@netflix.com', '9000000005', 'Hiring Manager at Netflix', 'Creating entertainment magic with talented engineers.', 'Bangalore, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Apple Recruitment', 'recruit@apple.com', '9000000006', 'Tech Recruiter at Apple', 'Looking for innovators to shape the future at Apple.', 'Hyderabad, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE),
(UNHEX(REPLACE(UUID(), '-', '')), 'Spotify Careers', 'careers@spotify.com', '9000000007', 'Talent Partner at Spotify', 'Helping build the future of audio at Spotify.', 'Delhi, India', NULL, NULL, NULL, NULL, NULL, NOW(), NOW(), FALSE);

-- =============================================
-- JOBS
-- =============================================

USE hireconnect_jobs;

INSERT INTO jobs (title, category, type, location, min_salary, max_salary, experience_required, description, status, posted_at, view_count) VALUES
('Senior Software Engineer', 'TECHNOLOGY', 'FULL_TIME', 'Bangalore, India', 2500000, 4500000, 5, 'We are looking for a Senior Software Engineer to join our core platform team. You will design and build scalable distributed systems that serve millions of users. Responsibilities include architecture design, code reviews, mentoring junior engineers, and collaborating with product teams.', 'ACTIVE', NOW(), 45),

('Frontend Developer', 'TECHNOLOGY', 'FULL_TIME', 'Hyderabad, India', 1500000, 2800000, 3, 'Join our frontend team to build beautiful user interfaces. You will work with modern frameworks like React and TypeScript to create responsive web applications. Collaborate with UX designers and backend engineers to deliver seamless user experiences.', 'ACTIVE', NOW(), 32),

('DevOps Engineer', 'TECHNOLOGY', 'FULL_TIME', 'Bangalore, India', 2000000, 3800000, 4, 'Looking for DevOps engineers to help scale our infrastructure. You will manage CI/CD pipelines, container orchestration with Kubernetes, and cloud infrastructure on AWS/Azure. Experience with Terraform and monitoring tools is required.', 'ACTIVE', NOW(), 28),

('Data Scientist', 'TECHNOLOGY', 'FULL_TIME', 'Chennai, India', 1800000, 3500000, 3, 'Join our data science team to solve complex business problems. You will build machine learning models, perform statistical analysis, and create data pipelines. Strong Python skills and experience with ML frameworks are essential.', 'ACTIVE', NOW(), 56),

('Product Manager', 'PRODUCT', 'FULL_TIME', 'Mumbai, India', 3000000, 5500000, 5, 'Leading product development for our next-generation features. You will define product strategy, create roadmaps, and work closely with engineering and design teams. Strong analytical skills and stakeholder management experience required.', 'ACTIVE', NOW(), 41),

('UX Designer', 'DESIGN', 'FULL_TIME', 'Bangalore, India', 1200000, 2400000, 3, 'Create engaging user experiences for millions of users worldwide. You will conduct user research, create wireframes and prototypes in Figma, and collaborate with developers to implement designs. Portfolio showcasing UI/UX projects is required.', 'ACTIVE', NOW(), 38),

('iOS Developer', 'TECHNOLOGY', 'FULL_TIME', 'Hyderabad, India', 2000000, 4000000, 4, 'Build the next generation of iOS applications. You will develop native iOS apps using Swift and SwiftUI, work on performance optimization, and implement new features. Experience with Core Data and RESTful APIs is required.', 'ACTIVE', NOW(), 22),

('Backend Engineer', 'TECHNOLOGY', 'REMOTE', 2200000, 4200000, 4, 'Help us deliver services to millions of users globally. You will build scalable microservices, design APIs, and work with distributed systems. Strong Java/Python skills and experience with Kafka or similar messaging systems.', 'ACTIVE', NOW(), 67),

('Full Stack Developer', 'TECHNOLOGY', 'HYBRID', 'Pune, India', 1800000, 3200000, 3, 'Join our dynamic team building innovative web applications. You will work across the entire stack from React frontend to Node.js backend. Experience with MongoDB and GraphQL is a plus.', 'ACTIVE', NOW(), 44),

('Machine Learning Engineer', 'TECHNOLOGY', 'FULL_TIME', 'Bangalore, India', 2500000, 4800000, 4, 'Work on cutting-edge ML models for recommendation systems. You will train and deploy deep learning models, optimize inference performance, and improve our personalization algorithms. PhD or Masters in CS/ML preferred.', 'ACTIVE', NOW(), 39),

('Cloud Architect', 'TECHNOLOGY', 'FULL_TIME', 'Delhi, India', 3500000, 6500000, 6, 'Design and implement cloud-native solutions for enterprise clients. You will lead cloud migration initiatives, design multi-cloud strategies, and establish best practices for security and scalability.', 'ACTIVE', NOW(), 31),

('Android Developer', 'TECHNOLOGY', 'FULL_TIME', 'Hyderabad, India', 1600000, 3000000, 3, 'Build features for millions of Android users. You will develop native Android apps using Kotlin and Jetpack Compose, implement offline capabilities, and optimize app performance.', 'ACTIVE', NOW(), 27);

-- Add skills for each job
INSERT INTO job_skills (job_id, skill) VALUES
(1, 'Java'), (1, 'Spring Boot'), (1, 'Microservices'), (1, 'AWS'),
(2, 'React'), (2, 'TypeScript'), (2, 'JavaScript'), (2, 'CSS'),
(3, 'Docker'), (3, 'Kubernetes'), (3, 'Azure'), (3, 'Terraform'),
(4, 'Python'), (4, 'Machine Learning'), (4, 'SQL'), (4, 'TensorFlow'),
(5, 'Product Strategy'), (5, 'Agile'), (5, 'Analytics'), (5, 'Leadership'),
(6, 'Figma'), (6, 'UI/UX'), (6, 'Prototyping'), (6, 'User Research'),
(7, 'Swift'), (7, 'iOS'), (7, 'Objective-C'), (7, 'Mobile Development'),
(8, 'Java'), (8, 'Python'), (8, 'Microservices'), (8, 'Kafka'),
(9, 'Node.js'), (9, 'React'), (9, 'MongoDB'), (9, 'GraphQL'),
(10, 'Python'), (10, 'PyTorch'), (10, 'Deep Learning'), (10, 'NLP'),
(11, 'Azure'), (11, 'AWS'), (11, 'GCP'), (11, 'Architecture'),
(12, 'Kotlin'), (12, 'Android'), (12, 'Jetpack Compose'), (12, 'Firebase');

SELECT 'Data seeding completed successfully!' AS status;