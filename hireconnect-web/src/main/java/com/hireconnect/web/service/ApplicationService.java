package com.hireconnect.web.service;

import com.hireconnect.web.dto.ApplicationDto;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    void applyForJob(UUID candidateId, Long jobId);

    List<ApplicationDto> getApplicationsByCandidate(UUID candidateId);

    List<ApplicationDto> getApplicationsForRecruiter(UUID recruiterId);

    void shortlistCandidate(Long applicationId);

    void rejectCandidate(Long applicationId);

    ApplicationDto getApplicationById(Long applicationId);

    void offerCandidate(Long applicationId);
}