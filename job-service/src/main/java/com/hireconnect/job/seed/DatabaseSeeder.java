package com.hireconnect.job.seed;

import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.enums.JobType;
import com.hireconnect.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final JobRepository jobRepository;

    @Override
    public void run(String... args) {
        if (jobRepository.count() == 0) {
            log.info("Seeding database with sample jobs...");
            seedJobs();
            log.info("Database seeding completed!");
        } else {
            log.info("Database already contains jobs, skipping seeding.");
        }
    }

    private void seedJobs() {
        UUID recruiter1 = UUID.fromString("602933df-bdc5-40e7-b9db-7ced2d14ede1");
        UUID recruiter2 = UUID.fromString("702933df-bdc5-40e7-b9db-7ced2d14ede2");

        List<Job> jobs = List.of(
            Job.builder()
                .title("Senior Java Developer")
                .company("Tech Solutions Inc.")
                .description("We are looking for an experienced Java developer to join our team. You will be working on enterprise-scale applications using Spring Boot, microservices architecture, and cloud technologies.\n\nResponsibilities:\n- Design and develop high-quality Java applications\n- Collaborate with cross-functional teams\n- Mentor junior developers\n- Participate in code reviews")
                .requirements("Java, Spring Boot, Hibernate, SQL, Microservices, Docker, Kubernetes")
                .location("Bangalore, India")
                .salaryRange("₹15L - ₹25L")
                .employmentType(JobType.FULL_TIME)
                .experienceLevel("Senior")
                .status(JobStatus.OPEN)
                .postedBy(recruiter1)
                .createdAt(LocalDateTime.now().minusDays(7))
                .updatedAt(LocalDateTime.now().minusDays(7))
                .build(),

            Job.builder()
                .title("React Frontend Developer")
                .company("Innovation Labs")
                .description("Join our dynamic frontend team building modern web applications. We use React, TypeScript, and cutting-edge UI frameworks to deliver exceptional user experiences.\n\nRequirements:\n- Strong React.js skills\n- Experience with Redux or Context API\n- Knowledge of RESTful APIs\n- Familiarity with CI/CD pipelines")
                .requirements("React, JavaScript, TypeScript, HTML5, CSS3, Redux, Git")
                .location("Hyderabad, India")
                .salaryRange("₹8L - ₹15L")
                .employmentType(JobType.FULL_TIME)
                .experienceLevel("Mid-Level")
                .status(JobStatus.OPEN)
                .postedBy(recruiter1)
                .createdAt(LocalDateTime.now().minusDays(12))
                .updatedAt(LocalDateTime.now().minusDays(12))
                .build(),

            Job.builder()
                .title("Full Stack Developer")
                .company("Digital Transformers")
                .description("Looking for a versatile full stack developer comfortable with both frontend and backend technologies. You will work on exciting projects for Fortune 500 clients.\n\nTech Stack:\n- Frontend: React/Vue.js\n- Backend: Node.js/Python\n- Database: PostgreSQL/MongoDB\n- Cloud: AWS/Azure")
                .requirements("React, Node.js, Python, PostgreSQL, MongoDB, AWS, Docker")
                .location("Remote")
                .salaryRange("₹12L - ₹20L")
                .employmentType(JobType.REMOTE)
                .experienceLevel("Mid-Level")
                .status(JobStatus.OPEN)
                .postedBy(recruiter2)
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build(),

            Job.builder()
                .title("DevOps Engineer")
                .company("Cloud Systems Pvt Ltd")
                .description("Join our infrastructure team to build and maintain scalable cloud infrastructure. Experience with Kubernetes, Terraform, and monitoring tools required.\n\nKey Responsibilities:\n- Manage CI/CD pipelines\n- Infrastructure as Code\n- Monitoring and alerting\n- Security compliance")
                .requirements("AWS, Azure, Kubernetes, Docker, Terraform, Jenkins, Prometheus")
                .location("Pune, India")
                .salaryRange("₹18L - ₹30L")
                .employmentType(JobType.FULL_TIME)
                .experienceLevel("Senior")
                .status(JobStatus.OPEN)
                .postedBy(recruiter1)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build(),

            Job.builder()
                .title("Data Scientist")
                .company("AI Innovations")
                .description("Help us build intelligent systems using machine learning and AI. You will work on NLP, computer vision, and predictive analytics projects.\n\nQualifications:\n- MSc/PhD in Computer Science or related field\n- 3+ years of ML experience\n- Strong Python skills\n- Published research is a plus")
                .requirements("Python, TensorFlow, PyTorch, Machine Learning, Deep Learning, SQL, Statistics")
                .location("Bangalore, India")
                .salaryRange("₹20L - ₹35L")
                .employmentType(JobType.FULL_TIME)
                .experienceLevel("Senior")
                .status(JobStatus.OPEN)
                .postedBy(recruiter2)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build(),

            Job.builder()
                .title("Mobile App Developer (Flutter)")
                .company("MobileFirst Solutions")
                .description("Build beautiful cross-platform mobile applications using Flutter. Work with designers and backend engineers to deliver amazing user experiences.\n\nWhat You'll Do:\n- Develop Flutter applications\n- Integrate with REST APIs\n- Implement state management\n- Optimize app performance")
                .requirements("Flutter, Dart, Firebase, REST APIs, Git, Mobile UI/UX")
                .location("Chennai, India")
                .salaryRange("₹6L - ₹12L")
                .employmentType(JobType.FULL_TIME)
                .experienceLevel("Junior")
                .status(JobStatus.OPEN)
                .postedBy(recruiter1)
                .createdAt(LocalDateTime.now().minusDays(8))
                .updatedAt(LocalDateTime.now().minusDays(8))
                .build(),

            Job.builder()
                .title("UX/UI Designer")
                .company("Creative Studio")
                .description("Design intuitive and visually appealing interfaces for web and mobile applications. Collaborate with product managers and developers to bring designs to life.\n\nRequirements:\n- Portfolio demonstrating strong design skills\n- Proficiency in Figma/Adobe XD\n- Understanding of user-centered design principles\n- Experience with design systems")
                .requirements("Figma, Adobe XD, UI Design, UX Research, Prototyping, Design Systems")
                .location("Mumbai, India")
                .salaryRange("₹8L - ₹16L")
                .employmentType(JobType.HYBRID)
                .experienceLevel("Mid-Level")
                .status(JobStatus.OPEN)
                .postedBy(recruiter2)
                .createdAt(LocalDateTime.now().minusDays(4))
                .updatedAt(LocalDateTime.now().minusDays(4))
                .build(),

            Job.builder()
                .title("Backend Engineer (Node.js)")
                .company("StartupXYZ")
                .description("Join our fast-growing startup as a backend engineer. You'll be responsible for designing and implementing scalable APIs and services.\n\nTech Stack:\n- Node.js, Express\n- MongoDB, Redis\n- RabbitMQ\n- Docker, Kubernetes")
                .requirements("Node.js, Express, MongoDB, Redis, REST APIs, Microservices, Docker")
                .location("Delhi, India")
                .salaryRange("₹10L - ₹18L")
                .employmentType(JobType.FULL_TIME)
                .experienceLevel("Mid-Level")
                .status(JobStatus.OPEN)
                .postedBy(recruiter1)
                .createdAt(LocalDateTime.now().minusDays(6))
                .updatedAt(LocalDateTime.now().minusDays(6))
                .build()
        );

        jobRepository.saveAll(jobs);
        log.info("Seeded {} jobs", jobs.size());
    }
}
