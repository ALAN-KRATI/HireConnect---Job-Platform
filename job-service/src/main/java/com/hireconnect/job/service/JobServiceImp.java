package com.hireconnect.job.service;

import com.hireconnect.job.client.AnalyticsClient;
import com.hireconnect.job.dto.JobRequest;
import com.hireconnect.job.dto.JobResponse;
import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.exception.JobNotFoundException;
import com.hireconnect.job.messaging.JobNotificationProducer;
import com.hireconnect.job.repository.JobRepository;
import com.hireconnect.job.util.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobServiceImp implements JobService {

    private final JobRepository repository;
    private final AnalyticsClient analyticsClient;
    private final JobNotificationProducer notificationProducer;
    private final JobMapper jobMapper;
    private final JobSearchService jobSearchService;

    @Override
    public JobResponse addJob(JobRequest request) {

        validateSalaryRange(request);

        Job job = jobMapper.toEntity(request);

        Job savedJob = repository.save(job);

        // Index in Elasticsearch
        jobSearchService.indexJob(savedJob);

        notificationProducer.sendJobCreated(savedJob.getJobId());

        return jobMapper.toResponse(savedJob);
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return repository.findByStatus(JobStatus.OPEN)
                .stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    public JobResponse getJobById(Long jobId) {
        return jobMapper.toResponse(findJobById(jobId));
    }

    @Override
    public JobResponse updateJob(Long jobId, JobRequest request) {

        validateSalaryRange(request);

        Job job = findJobById(jobId);

        job.setTitle(request.getTitle());
        job.setCategory(request.getCategory());
        job.setType(request.getType());
        job.setLocation(request.getLocation());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setDescription(request.getDescription());
        job.setSkills(request.getSkills());
        job.setExperienceRequired(request.getExperienceRequired());

        if (!job.getPostedBy().equals(request.getPostedBy())) {
            throw new IllegalArgumentException("You are not allowed to update another recruiter's job");
        }

        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        Job updatedJob = repository.save(job);

        // Update in Elasticsearch
        jobSearchService.indexJob(jobMapper.toResponse(updatedJob));

        return jobMapper.toResponse(updatedJob);
    }

    @Override
    public void deleteJob(Long jobId) {

        Job job = findJobById(jobId);

        job.setStatus(JobStatus.DELETED);

        repository.save(job);

        // Remove from Elasticsearch
        jobSearchService.deleteJobIndex(jobId);

        notificationProducer.sendJobStatusChanged(jobId, JobStatus.DELETED.name());
    }

    @Override
    public List<JobResponse> getJobsByCategory(String category) {
        return repository.findByCategoryIgnoreCase(category)
                .stream()
                .filter(job -> job.getStatus() == JobStatus.OPEN)
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    public List<JobResponse> getJobsByLocation(String location) {
        return repository.findByLocationIgnoreCase(location)
                .stream()
                .filter(job -> job.getStatus() == JobStatus.OPEN)
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    public List<JobResponse> getJobsByRecruiter(Long recruiterId) {
        return repository.findByPostedBy(recruiterId)
                .stream()
                .filter(job -> job.getStatus() != JobStatus.DELETED)
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    public List<JobResponse> searchJobs(
            String title,
            String location,
            String category,
            Double minSalary,
            Double maxSalary,
            Integer experience) {

        // Use Elasticsearch for efficient search
        return jobSearchService.searchJobs(title, location, category, minSalary, maxSalary, experience);
    }

    @Override
    public JobResponse changeStatus(Long jobId, String status) {

        Job job = findJobById(jobId);

        try {
            job.setStatus(JobStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid status. Allowed values: OPEN, CLOSED, PAUSED, DELETED");
        }

        Job updatedJob = repository.save(job);

        notificationProducer.sendJobStatusChanged(
                updatedJob.getJobId(),
                updatedJob.getStatus().name());

        return jobMapper.toResponse(updatedJob);
    }

    // JobServiceImpl.java
    @Override
    public Long getJobViewCount(Long jobId) {

        Job job = repository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        return job.getViewCount() == null ? 0L : job.getViewCount();
    }

    @Override
    public Long getRecruiterJobCount(Long recruiterId) {
        return repository.countByPostedBy(recruiterId);
    }

    @Override
    public Map<String, Long> getTopCategories() {

        Map<String, Long> topCategories = new LinkedHashMap<>();

        List<String> categories = repository.findAll()
                .stream()
                .map(Job::getCategory)
                .distinct()
                .toList();

        for (String category : categories) {
            topCategories.put(category, repository.countByCategoryIgnoreCase(category));
        }

        return topCategories;
    }

    private Job findJobById(Long jobId) {
        return repository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));
    }

    private void validateSalaryRange(JobRequest request) {
        if (request.getMinSalary() > request.getMaxSalary()) {
            throw new IllegalArgumentException(
                    "Minimum salary cannot be greater than maximum salary");
        }
    }

    @Override
    public Long getRecruiterJobCountByStatus(Long recruiterId, JobStatus status) {
        return repository.countByPostedByAndStatus(recruiterId, status);
    }
}