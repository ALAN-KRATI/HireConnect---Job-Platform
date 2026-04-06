package com.hireconnect.interview.service;

import com.hireconnect.interview.entity.Interview;
import com.hireconnect.interview.enums.InterviewStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewService {

    Interview scheduleInterview(Interview interview);

    String confirmInterview(Long interviewId);

    Interview rescheduleInterview(Long interviewId, LocalDateTime newDateTime);

    String cancelInterview(Long interviewId);

    List<Interview> getByApplication(Long applicationId);

    List<Interview> getByStatus(InterviewStatus status);

    Optional<Interview> getById(Long interviewId);
}