package com.hireconnect.web.service;

import com.hireconnect.web.dto.JobDto;

import java.util.List;

public interface JobService {

    List<JobDto> getAllJobs();

    JobDto getJobById(Long jobId);

    List<JobDto> searchJobs(String keyword, String location);

    List<JobDto> getJobsByRecruiter(Long recruiterId);

    JobDto createJob(JobDto jobDto);

    JobDto updateJob(Long jobId, JobDto jobDto);

    void deleteJob(Long jobId);

    List<JobDto> getSavedJobs(Long candidateId);
}