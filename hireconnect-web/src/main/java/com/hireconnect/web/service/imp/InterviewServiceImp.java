package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.InterviewDto;
import com.hireconnect.web.service.InterviewService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class InterviewServiceImp implements InterviewService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://interview-service/interviews";

    public InterviewServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void scheduleInterview(InterviewDto dto) {

        restTemplate.postForObject(
                BASE_URL,
                dto,
                Void.class
        );
    }

    @Override
    public List<InterviewDto> getCandidateInterviews(Long candidateId) {

        ResponseEntity<List<InterviewDto>> response = restTemplate.exchange(
                BASE_URL + "/candidate/{candidateId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InterviewDto>>() {},
                candidateId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public List<InterviewDto> getRecruiterInterviews(Long recruiterId) {

        ResponseEntity<List<InterviewDto>> response = restTemplate.exchange(
                BASE_URL + "/recruiter/{recruiterId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InterviewDto>>() {},
                recruiterId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public InterviewDto getInterviewById(Long interviewId) {

        return restTemplate.getForObject(
                BASE_URL + "/{interviewId}",
                InterviewDto.class,
                interviewId
        );
    }

    @Override
    public void cancelInterview(Long interviewId) {

        restTemplate.postForObject(
                BASE_URL + "/{interviewId}/cancel",
                null,
                Void.class,
                interviewId
        );
    }
}