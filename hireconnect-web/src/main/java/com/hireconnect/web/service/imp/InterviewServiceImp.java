package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.InterviewDto;
import com.hireconnect.web.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class InterviewServiceImp implements InterviewService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8085/interviews";

    @Override
    public void scheduleInterview(InterviewDto dto) {
        restTemplate.postForObject(BASE_URL, dto, Void.class);
    }

    @Override
    public List<InterviewDto> getCandidateInterviews(Long candidateId) {
        InterviewDto[] response = restTemplate.getForObject(BASE_URL + "/candidate/" + candidateId,
                InterviewDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public List<InterviewDto> getRecruiterInterviews(Long recruiterId) {
        InterviewDto[] response = restTemplate.getForObject(BASE_URL + "/recruiter/" + recruiterId,
                InterviewDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public InterviewDto getInterviewById(Long interviewId) {
        return restTemplate.getForObject(
                BASE_URL + "/" + interviewId,
                InterviewDto.class);
    }

    @Override
    public void cancelInterview(Long interviewId) {
        restTemplate.postForObject(
                BASE_URL + "/" + interviewId + "/cancel",
                null,
                Void.class);
    }
}