package com.hireconnect.job.service;

import com.hireconnect.job.document.JobDocument;
import com.hireconnect.job.dto.JobRequest;
import com.hireconnect.job.dto.JobResponse;
import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobSearchService {

    void indexJob(Job job);

    void indexJob(JobResponse jobResponse);

    void deleteJobIndex(Long jobId);

    void updateJobIndex(Long jobId, JobRequest request);

    List<JobResponse> searchJobs(String keyword);

    List<JobResponse> searchJobs(String title, String location, String category,
                                  Double minSalary, Double maxSalary, Integer experience);

    List<JobResponse> findByLocation(String location);

    List<JobResponse> findByCategory(String category);

    List<JobResponse> findBySalaryRange(Double minSalary, Double maxSalary);

    List<JobResponse> findByMinimumExperience(Integer experience);

    Page<JobResponse> findAllOpenJobs(Pageable pageable);

    List<JobResponse> advancedSearch(String query, List<String> skills,
                                      String location, Double minSalary, Double maxSalary);
}
