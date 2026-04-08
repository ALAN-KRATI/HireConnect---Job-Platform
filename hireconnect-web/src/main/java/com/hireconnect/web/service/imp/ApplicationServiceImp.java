package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.ApplicationDto;
import com.hireconnect.web.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ApplicationServiceImp implements ApplicationService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8084/applications";

    @Override
    public void applyForJob(Long candidateId, Long jobId) {
        restTemplate.postForObject(BASE_URL + "/apply?candidateId=" + candidateId + "&jobId=" + jobId, null,
                Void.class);
    }

    @Override
    public List<ApplicationDto> getApplicationsByCandidate(Long candidateId) {
        ApplicationDto[] response = restTemplate.getForObject(BASE_URL + "/candidate/" + candidateId,
                ApplicationDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public List<ApplicationDto> getApplicationsForRecruiter(Long recruiterId) {
        ApplicationDto[] response = restTemplate.getForObject(BASE_URL + "/recruiter/" + recruiterId,
                ApplicationDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public void shortlistCandidate(Long id) {
        restTemplate.postForObject(BASE_URL + "/" + id + "/shortlist", null, Void.class);
    }

    @Override
    public void rejectCandidate(Long id) {
        restTemplate.postForObject(BASE_URL + "/" + id + "/reject", null, Void.class);
    }

    @Override
    public ApplicationDto getApplicationById(Long applicationId) {
        return restTemplate.getForObject(
                BASE_URL + "/" + applicationId,
                ApplicationDto.class);
    }
}