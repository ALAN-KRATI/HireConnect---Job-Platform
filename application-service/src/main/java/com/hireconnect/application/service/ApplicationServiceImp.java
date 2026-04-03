package com.hireconnect.application.service;

import com.hireconnect.application.entity.Application;
import com.hireconnect.application.enums.ApplicationStatus;
import com.hireconnect.application.repository.ApplicationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImp implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RabbitTemplate rabbitTemplate;

    public ApplicationServiceImp(ApplicationRepository applicationRepository,
                                 RabbitTemplate rabbitTemplate) {
        this.applicationRepository = applicationRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Application submitApplication(Application application) {

        applicationRepository
                .findFirstByJobIdAndCandidateId(
                        application.getJobId(),
                        application.getCandidateId()
                )
                .ifPresent(existing -> {
                    throw new RuntimeException("Candidate has already applied for this job");
                });

        application.setAppliedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication = applicationRepository.save(application);

        rabbitTemplate.convertAndSend(
                "application.exchange",
                "application.submitted",
                savedApplication
        );

        return savedApplication;
    }

    @Override
    public List<Application> getByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    @Override
    public List<Application> getByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    @Override
    public Application updateStatus(Long applicationId, ApplicationStatus status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found with id: " + applicationId));

        application.setStatus(status);

        Application updatedApplication = applicationRepository.save(application);

        rabbitTemplate.convertAndSend(
                "application.exchange",
                "application.status.updated",
                updatedApplication
        );

        return updatedApplication;
    }

    @Override
    public void withdrawApplication(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found with id: " + applicationId));

        application.setStatus(ApplicationStatus.WITHDRAW);

        applicationRepository.save(application);

        rabbitTemplate.convertAndSend(
                "application.exchange",
                "application.withdrawn",
                application
        );
    }

    @Override
    public Application getById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found with id: " + applicationId));
    }

    @Override
    public int countByJob(Long jobId) {
        return applicationRepository.countByJobId(jobId);
    }
}