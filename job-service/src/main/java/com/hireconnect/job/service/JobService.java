package com.hireconnect.job.service;

import java.util.List;

import com.hireconnect.job.dto.JobRequest;
import com.hireconnect.job.dto.JobResponse;

public interface JobService {
    JobResponse addJob(JobRequest job);

    List<JobResponse> getAllJobs();

    JobResponse getJobById(Long id);

    JobResponse updateJob(Long id, JobRequest job);

    void deleteJob(Long id);

    List<JobResponse> getJobsByCategory(String category);

    List<JobResponse> getJobsByLocation(String location);

    List<JobResponse> searchJobs(String title, String location, String category, Double minSalary, Double maxSalary, Integer experience);

    JobResponse changeStatus(Long id, String status);

    List<JobResponse> getJobsByRecruiter(Long id);

}
