package com.hireconnect.web.service;

import com.hireconnect.web.dto.InterviewDto;

import java.util.List;
import java.util.UUID;

public interface InterviewService {

    void scheduleInterview(InterviewDto interviewDto);

    List<InterviewDto> getCandidateInterviews(UUID candidateId);

    List<InterviewDto> getRecruiterInterviews(UUID recruiterId);

    void cancelInterview(Long interviewId);

    InterviewDto getInterviewById(Long interviewId);
}