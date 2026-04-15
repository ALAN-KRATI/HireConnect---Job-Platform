package com.hireconnect.application.service;

import com.hireconnect.application.entity.Application;
import com.hireconnect.application.enums.ApplicationStatus;
import com.hireconnect.application.event.ApplicationStatusChangedEvent;
import com.hireconnect.application.exception.ApplicationNotFoundException;
import com.hireconnect.application.exception.DuplicateApplicationException;
import com.hireconnect.application.repository.ApplicationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
                    throw new DuplicateApplicationException(
                            "Candidate has already applied for this job"
                    );
                });

        if (application.getApplicationId() == null) {
            application.setApplicationId(UUID.randomUUID());
        }

        application.setAppliedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication = applicationRepository.save(application);

        rabbitTemplate.convertAndSend(
                "application.exchange",
                "application.submitted",
                new ApplicationStatusChangedEvent(
                        savedApplication.getApplicationId(),
                        savedApplication.getCandidateId(),
                        savedApplication.getRecruiterId(),
                        savedApplication.getJobId(),
                        savedApplication.getStatus(),
                        savedApplication.getUpdatedAt()
                )
        );

        return savedApplication;
    }

    @Override
    public List<Application> getByCandidate(UUID candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    @Override
    public List<Application> getByRecruiter(UUID recruiterId) {
        return applicationRepository.findByRecruiterId(recruiterId);
    }

    @Override
    public List<Application> getByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    @Override
    public Application getById(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new ApplicationNotFoundException(
                                "Application not found with id: " + applicationId
                        ));
    }

    @Override
    public Application updateStatus(UUID applicationId, ApplicationStatus status) {

        Application application = getById(applicationId);

        application.setStatus(status);
        application.setUpdatedAt(LocalDateTime.now());

        Application updatedApplication = applicationRepository.save(application);

        rabbitTemplate.convertAndSend(
                "application.exchange",
                "application.status.updated",
                new ApplicationStatusChangedEvent(
                        updatedApplication.getApplicationId(),
                        updatedApplication.getCandidateId(),
                        updatedApplication.getRecruiterId(),
                        updatedApplication.getJobId(),
                        updatedApplication.getStatus(),
                        updatedApplication.getUpdatedAt()
                )
        );

        return updatedApplication;
    }

    @Override
    public void withdrawApplication(UUID applicationId) {

        Application application = getById(applicationId);

        application.setStatus(ApplicationStatus.WITHDRAW);
        application.setUpdatedAt(LocalDateTime.now());

        Application updatedApplication = applicationRepository.save(application);

        rabbitTemplate.convertAndSend(
                "application.exchange",
                "application.withdrawn",
                new ApplicationStatusChangedEvent(
                        updatedApplication.getApplicationId(),
                        updatedApplication.getCandidateId(),
                        updatedApplication.getRecruiterId(),
                        updatedApplication.getJobId(),
                        updatedApplication.getStatus(),
                        updatedApplication.getUpdatedAt()
                )
        );
    }

    @Override
    public long countByJob(Long jobId) {
        return applicationRepository.countByJobId(jobId);
    }

    @Override
    public long countByRecruiterId(UUID recruiterId) {
        return applicationRepository.countByRecruiterId(recruiterId);
    }

    @Override
    public long countByRecruiterIdAndStatus(UUID recruiterId, ApplicationStatus status) {
        return applicationRepository.countByRecruiterIdAndStatus(recruiterId, status);
    }

    @Override
    public long countByStatus(ApplicationStatus status) {
        return applicationRepository.countByStatus(status);
    }

    @Override
    public long count() {
        return applicationRepository.count();
    }

    @Override
    public Double findAverageTimeToHireByRecruiterId(UUID recruiterId) {
        Double avg = applicationRepository.findAverageTimeToHireByRecruiterId(recruiterId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public Double findPlatformAverageTimeToHire() {
        Double avg = applicationRepository.findPlatformAverageTimeToHire();
        return avg != null ? avg : 0.0;
    }

    @Override
    public List<Application> getByCandidateEmail(String email) {
        return applicationRepository.findByCandidateEmail(email);
    }
}