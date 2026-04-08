package com.hireconnect.web.service;

import com.hireconnect.web.dto.InterviewDto;

import java.util.List;

public interface InterviewService {

    void scheduleInterview(InterviewDto interviewDto);

    List<InterviewDto> getCandidateInterviews(Long candidateId);

    List<InterviewDto> getRecruiterInterviews(Long recruiterId);

    void cancelInterview(Long interviewId);

    InterviewDto getInterviewById(Long interviewId);
}