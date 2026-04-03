package com.hireconnect.job.service;

import com.hireconnect.job.dto.JobRequest;
import com.hireconnect.job.dto.JobResponse;
import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.exception.JobNotFoundException;
import com.hireconnect.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImp implements JobService {

    private final JobRepository repository;

    @Override
    public JobResponse addJob(JobRequest job) {

        Job j = Job.builder()
                .title(job.getTitle())
                .category(job.getCategory())
                .type(job.getType())
                .location(job.getLocation())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .description(job.getDescription())
                .skills(job.getSkills())
                .experienceRequired(job.getExperienceRequired())
                .postedBy(job.getPostedBy())
                .status(job.getStatus() != null ? job.getStatus() : JobStatus.ACTIVE)
                .postedAt(job.getPostedAt() != null ? job.getPostedAt() : LocalDateTime.now())
                .build();

        Job savedJob = repository.save(j);

        return mapToResponse(savedJob);
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return repository.findByStatus(JobStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public JobResponse getJobById(Long id) {

        Job job = repository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        return mapToResponse(job);
    }

    @Override
    public JobResponse updateJob(Long id, JobRequest request) {

        Job job = repository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        job.setTitle(request.getTitle());
        job.setCategory(request.getCategory());
        job.setType(request.getType());
        job.setLocation(request.getLocation());
        job.setMinSalary(request.getMinSalary());
        job.setMaxSalary(request.getMaxSalary());
        job.setDescription(request.getDescription());
        job.setSkills(request.getSkills());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setPostedBy(request.getPostedBy());

        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        Job updatedJob = repository.save(job);

        return mapToResponse(updatedJob);
    }

    @Override
    public void deleteJob(Long id) {

        Job job = repository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        job.setStatus(JobStatus.DELETED);

        repository.save(job);
    }

    @Override
    public List<JobResponse> getJobsByCategory(String category) {

        return repository.findByCategoryIgnoreCase(category)
                .stream()
                .filter(job -> job.getStatus() == JobStatus.ACTIVE)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<JobResponse> getJobsByLocation(String location) {

        return repository.findByLocationIgnoreCase(location)
                .stream()
                .filter(job -> job.getStatus() == JobStatus.ACTIVE)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<JobResponse> searchJobs(String title,
                                        String location,
                                        String category,
                                        Double minSalary,
                                        Double maxSalary,
                                        Integer experience) {

        return repository.findAll()
                .stream()
                .filter(job -> job.getStatus() == JobStatus.ACTIVE)
                .filter(job -> title == null
                        || title.isBlank()
                        || job.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(job -> location == null
                        || location.isBlank()
                        || job.getLocation().equalsIgnoreCase(location))
                .filter(job -> category == null
                        || category.isBlank()
                        || job.getCategory().equalsIgnoreCase(category))
                .filter(job -> minSalary == null
                        || job.getMinSalary() >= minSalary)
                .filter(job -> maxSalary == null
                        || job.getMaxSalary() <= maxSalary)
                .filter(job -> experience == null
                        || job.getExperienceRequired() >= experience)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public JobResponse changeStatus(Long id, String status) {

        Job job = repository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        try {
            job.setStatus(JobStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status. Allowed values: ACTIVE, CLOSED, DELETED"
            );
        }

        Job updatedJob = repository.save(job);

        return mapToResponse(updatedJob);
    }

    @Override
    public List<JobResponse> getJobsByRecruiter(Long id) {

        return repository.findByPostedBy(id)
                .stream()
                .filter(job -> job.getStatus() == JobStatus.ACTIVE)
                .map(this::mapToResponse)
                .toList();
    }

    private JobResponse mapToResponse(Job job) {

        return JobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .category(job.getCategory())
                .type(job.getType())
                .location(job.getLocation())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .description(job.getDescription())
                .skills(job.getSkills())
                .experienceRequired(job.getExperienceRequired())
                .postedBy(job.getPostedBy())
                .status(job.getStatus())
                .postedAt(job.getPostedAt())
                .build();
    }
}