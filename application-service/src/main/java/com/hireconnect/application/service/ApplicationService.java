package com.hireconnect.application.service;

import java.util.List;

import com.hireconnect.application.entity.Application;
import com.hireconnect.application.enums.ApplicationStatus;

public interface ApplicationService {
    Application submitApplication(Application application);

    List<Application> getByCandidate(Long candidateId);

    List<Application> getByJob(Long jobId);

    Application updateStatus(Long applicationId, ApplicationStatus status);

    void withdrawApplication(Long applicationId);

    Application getById(Long applicationId);

    int countByJob(Long jobId);

}
