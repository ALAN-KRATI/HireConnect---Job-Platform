-- =============================================
-- HireConnect Jobs Seeder Only
-- Run this if users already exist
-- =============================================

USE hireconnect_jobs;

-- Clear existing jobs first
DELETE FROM job_skills WHERE job_id IN (SELECT job_id FROM jobs);
DELETE FROM jobs;

INSERT INTO jobs (title, category, type, location, min_salary, max_salary, experience_required, description, status, posted_at, view_count, posted_by) VALUES
('Senior Software Engineer', 'TECHNOLOGY', 'FULL_TIME', 'Bangalore, India', 2500000, 4500000, 5, 'We are looking for a Senior Software Engineer to join our core platform team. You will design and build scalable distributed systems that serve millions of users. Responsibilities include architecture design, code reviews, mentoring junior engineers, and collaborating with product teams.', 'OPEN', NOW(), 45, 1),

('Frontend Developer', 'TECHNOLOGY', 'FULL_TIME', 'Hyderabad, India', 1500000, 2800000, 3, 'Join our frontend team to build beautiful user interfaces. You will work with modern frameworks like React and TypeScript to create responsive web applications. Collaborate with UX designers and backend engineers to deliver seamless user experiences.', 'OPEN', NOW(), 32, 1),

('DevOps Engineer', 'TECHNOLOGY', 'FULL_TIME', 'Bangalore, India', 2000000, 3800000, 4, 'Looking for DevOps engineers to help scale our infrastructure. You will manage CI/CD pipelines, container orchestration with Kubernetes, and cloud infrastructure on AWS/Azure. Experience with Terraform and monitoring tools is required.', 'OPEN', NOW(), 28, 2),

('Data Scientist', 'TECHNOLOGY', 'FULL_TIME', 'Chennai, India', 1800000, 3500000, 3, 'Join our data science team to solve complex business problems. You will build machine learning models, perform statistical analysis, and create data pipelines. Strong Python skills and experience with ML frameworks are essential.', 'OPEN', NOW(), 56, 2),

('Product Manager', 'PRODUCT', 'FULL_TIME', 'Mumbai, India', 3000000, 5500000, 5, 'Leading product development for our next-generation features. You will define product strategy, create roadmaps, and work closely with engineering and design teams. Strong analytical skills and stakeholder management experience required.', 'OPEN', NOW(), 41, 3),

('UX Designer', 'DESIGN', 'FULL_TIME', 'Bangalore, India', 1200000, 2400000, 3, 'Create engaging user experiences for millions of users worldwide. You will conduct user research, create wireframes and prototypes in Figma, and collaborate with developers to implement designs. Portfolio showcasing UI/UX projects is required.', 'OPEN', NOW(), 38, 3),

('iOS Developer', 'TECHNOLOGY', 'FULL_TIME', 'Hyderabad, India', 2000000, 4000000, 4, 'Build the next generation of iOS applications. You will develop native iOS apps using Swift and SwiftUI, work on performance optimization, and implement new features. Experience with Core Data and RESTful APIs is required.', 'OPEN', NOW(), 22, 4),

('Backend Engineer', 'TECHNOLOGY', 'REMOTE', 'Remote, India', 2200000, 4200000, 4, 'Help us deliver services to millions of users globally. You will build scalable microservices, design APIs, and work with distributed systems. Strong Java/Python skills and experience with Kafka or similar messaging systems.', 'OPEN', NOW(), 67, 4),

('Full Stack Developer', 'TECHNOLOGY', 'HYBRID', 'Pune, India', 1800000, 3200000, 3, 'Join our dynamic team building innovative web applications. You will work across the entire stack from React frontend to Node.js backend. Experience with MongoDB and GraphQL is a plus.', 'OPEN', NOW(), 44, 5),

('Machine Learning Engineer', 'TECHNOLOGY', 'FULL_TIME', 'Bangalore, India', 2500000, 4800000, 4, 'Work on cutting-edge ML models for recommendation systems. You will train and deploy deep learning models, optimize inference performance, and improve our personalization algorithms. PhD or Masters in CS/ML preferred.', 'OPEN', NOW(), 39, 5),

('Cloud Architect', 'TECHNOLOGY', 'FULL_TIME', 'Delhi, India', 3500000, 6500000, 6, 'Design and implement cloud-native solutions for enterprise clients. You will lead cloud migration initiatives, design multi-cloud strategies, and establish best practices for security and scalability.', 'OPEN', NOW(), 31, 6),

('Android Developer', 'TECHNOLOGY', 'FULL_TIME', 'Hyderabad, India', 1600000, 3000000, 3, 'Build features for millions of Android users. You will develop native Android apps using Kotlin and Jetpack Compose, implement offline capabilities, and optimize app performance.', 'OPEN', NOW(), 27, 7);

-- Add skills for each job using INSERT IGNORE to avoid duplicates
SET @job1 = LAST_INSERT_ID();
SET @job2 = @job1 + 1;
SET @job3 = @job1 + 2;
SET @job4 = @job1 + 3;
SET @job5 = @job1 + 4;
SET @job6 = @job1 + 5;
SET @job7 = @job1 + 6;
SET @job8 = @job1 + 7;
SET @job9 = @job1 + 8;
SET @job10 = @job1 + 9;
SET @job11 = @job1 + 10;
SET @job12 = @job1 + 11;

INSERT INTO job_skills (job_id, skill) VALUES
(@job1, 'Java'), (@job1, 'Spring Boot'), (@job1, 'Microservices'), (@job1, 'AWS'),
(@job2, 'React'), (@job2, 'TypeScript'), (@job2, 'JavaScript'), (@job2, 'CSS'),
(@job3, 'Docker'), (@job3, 'Kubernetes'), (@job3, 'Azure'), (@job3, 'Terraform'),
(@job4, 'Python'), (@job4, 'Machine Learning'), (@job4, 'SQL'), (@job4, 'TensorFlow'),
(@job5, 'Product Strategy'), (@job5, 'Agile'), (@job5, 'Analytics'), (@job5, 'Leadership'),
(@job6, 'Figma'), (@job6, 'UI/UX'), (@job6, 'Prototyping'), (@job6, 'User Research'),
(@job7, 'Swift'), (@job7, 'iOS'), (@job7, 'Objective-C'), (@job7, 'Mobile Development'),
(@job8, 'Java'), (@job8, 'Python'), (@job8, 'Microservices'), (@job8, 'Kafka'),
(@job9, 'Node.js'), (@job9, 'React'), (@job9, 'MongoDB'), (@job9, 'GraphQL'),
(@job10, 'Python'), (@job10, 'PyTorch'), (@job10, 'Deep Learning'), (@job10, 'NLP'),
(@job11, 'Azure'), (@job11, 'AWS'), (@job11, 'GCP'), (@job11, 'Architecture'),
(@job12, 'Kotlin'), (@job12, 'Android'), (@job12, 'Jetpack Compose'), (@job12, 'Firebase');

SELECT CONCAT('✅ Successfully created ', (SELECT COUNT(*) FROM jobs), ' jobs!') AS status;