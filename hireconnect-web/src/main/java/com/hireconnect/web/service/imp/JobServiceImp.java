package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.JobDto;
import com.hireconnect.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class JobServiceImp implements JobService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8083/jobs";

    @Override
    public List<JobDto> getAllJobs() {
        JobDto[] response = restTemplate.getForObject(BASE_URL, JobDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public JobDto getJobById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, JobDto.class);
    }

    @Override
    public List<JobDto> getJobsByRecruiter(Long recruiterId) {
        JobDto[] response = restTemplate.getForObject(BASE_URL + "/recruiter/" + recruiterId, JobDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public JobDto createJob(JobDto dto) {
        return restTemplate.postForObject(BASE_URL, dto, JobDto.class);
    }

    @Override
    public JobDto updateJob(Long id, JobDto dto) {
        restTemplate.put(BASE_URL + "/" + id, dto);
        return dto;
    }

    @Override
    public void deleteJob(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }

    @Override
    public List<JobDto> searchJobs(String keyword, String location) {
        JobDto[] response = restTemplate.getForObject(
                BASE_URL + "/search?keyword=" + keyword + "&location=" + location,
                JobDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public List<JobDto> getSavedJobs(Long candidateId) {
        JobDto[] response = restTemplate.getForObject(
                BASE_URL + "/saved/" + candidateId,
                JobDto[].class);

        return Arrays.asList(response);
    }
}