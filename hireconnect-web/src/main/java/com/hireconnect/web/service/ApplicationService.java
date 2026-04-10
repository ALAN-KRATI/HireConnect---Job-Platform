package com.hireconnect.web.service;

import com.hireconnect.web.dto.ApplicationDto;

import java.util.List;

public interface ApplicationService {

    void applyForJob(Long candidateId, Long jobId);

    List<ApplicationDto> getApplicationsByCandidate(Long candidateId);

    List<ApplicationDto> getApplicationsForRecruiter(Long recruiterId);

    void shortlistCandidate(Long applicationId);

    void rejectCandidate(Long applicationId);

    ApplicationDto getApplicationById(Long applicationId);

    void offerCandidate(Long applicationId);
}