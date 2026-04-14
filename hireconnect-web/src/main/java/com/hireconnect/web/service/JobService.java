package com.hireconnect.web.service;

import com.hireconnect.web.dto.JobDto;

import java.util.List;
import java.util.UUID;

public interface JobService {

    List<JobDto> getAllJobs();

    void closeJob(Long jobId);

    void pauseJob(Long jobId);

    JobDto getJobById(Long jobId);

    List<JobDto> searchJobs(String keyword, String location);

    List<JobDto> getJobsByRecruiter(UUID recruiterId);

    JobDto createJob(JobDto jobDto);

    JobDto updateJob(Long jobId, JobDto jobDto);

    void deleteJob(Long jobId);

    List<JobDto> getSavedJobs(UUID candidateId);
}