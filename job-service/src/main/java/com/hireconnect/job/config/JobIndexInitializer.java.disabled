package com.hireconnect.job.config;

import com.hireconnect.job.entity.Job;
import com.hireconnect.job.repository.JobRepository;
import com.hireconnect.job.service.JobSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobIndexInitializer {

    private final JobRepository jobRepository;
    private final JobSearchService jobSearchService;

    @Bean
    public CommandLineRunner initializeJobIndex() {
        return args -> {
            log.info("Job index initialization disabled for local development");
            // Temporarily disabled - Elasticsearch mapping issues
            /*
            log.info("Starting job index initialization...");

            try {
                List<Job> allJobs = jobRepository.findAll();
                log.info("Found {} jobs to index", allJobs.size());

                int count = 0;
                for (Job job : allJobs) {
                    try {
                        jobSearchService.indexJob(job);
                        count++;
                    } catch (Exception e) {
                        log.error("Failed to index job {}: {}", job.getJobId(), e.getMessage());
                    }
                }

                log.info("Successfully indexed {}/{} jobs in Elasticsearch", count, allJobs.size());
            } catch (Exception e) {
                log.error("Failed to initialize job index: {}", e.getMessage());
            }
            */
        };
    }
}