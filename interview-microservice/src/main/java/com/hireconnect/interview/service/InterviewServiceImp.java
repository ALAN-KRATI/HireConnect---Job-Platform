package com.hireconnect.interview.service;

import com.hireconnect.interview.entity.Interview;
import com.hireconnect.interview.enums.InterviewStatus;
import com.hireconnect.interview.event.InterviewNotificationEvent;
import com.hireconnect.interview.exception.InterviewAlreadyExistsException;
import com.hireconnect.interview.exception.InterviewNotFoundException;
import com.hireconnect.interview.repository.InterviewRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InterviewServiceImp implements InterviewService {

    private final InterviewRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public InterviewServiceImp(InterviewRepository repository,
                               RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Interview scheduleInterview(Interview interview) {

        if (repository.existsByApplicationId(interview.getApplicationId())) {
            throw new InterviewAlreadyExistsException(
                    "Interview already exists for this application"
            );
        }

        boolean slotTaken = repository.existsByScheduledAtAndRecruiterIdAndStatusNot(
                interview.getScheduledAt(),
                interview.getRecruiterId(),
                InterviewStatus.CANCELLED
        );

        if (slotTaken) {
            throw new InterviewAlreadyExistsException(
                    "Recruiter already has an interview scheduled at this time"
            );
        }

        interview.setInterviewId(UUID.randomUUID());
        interview.setStatus(InterviewStatus.SCHEDULED);

        Interview saved = repository.save(interview);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "interview.scheduled",
                new InterviewNotificationEvent(
                        saved.getInterviewId(),
                        saved.getApplicationId(),
                        saved.getCandidateId(),
                        saved.getRecruiterId(),
                        saved.getScheduledAt(),
                        saved.getStatus(),
                        "Interview scheduled for " + saved.getScheduledAt()
                )
        );

        return saved;
    }

    @Override
    public Interview confirmInterview(UUID interviewId) {

        Interview interview = getById(interviewId);

        interview.setStatus(InterviewStatus.CONFIRMED);

        Interview saved = repository.save(interview);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "interview.confirmed",
                new InterviewNotificationEvent(
                        saved.getInterviewId(),
                        saved.getApplicationId(),
                        saved.getCandidateId(),
                        saved.getRecruiterId(),
                        saved.getScheduledAt(),
                        saved.getStatus(),
                        "Interview confirmed"
                )
        );

        return saved;
    }

    @Override
    public Interview rescheduleInterview(UUID interviewId,
                                         LocalDateTime newDateTime) {

        Interview interview = getById(interviewId);

        boolean slotTaken = repository.existsByScheduledAtAndRecruiterIdAndStatusNot(
                newDateTime,
                interview.getRecruiterId(),
                InterviewStatus.CANCELLED
        );

        if (slotTaken) {
            throw new InterviewAlreadyExistsException(
                    "Recruiter already has an interview scheduled at this time"
            );
        }

        interview.setScheduledAt(newDateTime);
        interview.setStatus(InterviewStatus.RESCHEDULED);

        Interview saved = repository.save(interview);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "interview.rescheduled",
                new InterviewNotificationEvent(
                        saved.getInterviewId(),
                        saved.getApplicationId(),
                        saved.getCandidateId(),
                        saved.getRecruiterId(),
                        saved.getScheduledAt(),
                        saved.getStatus(),
                        "Interview rescheduled to " + saved.getScheduledAt()
                )
        );

        return saved;
    }

    @Override
    public void cancelInterview(UUID interviewId) {

        Interview interview = getById(interviewId);

        interview.setStatus(InterviewStatus.CANCELLED);

        Interview saved = repository.save(interview);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "interview.cancelled",
                new InterviewNotificationEvent(
                        saved.getInterviewId(),
                        saved.getApplicationId(),
                        saved.getCandidateId(),
                        saved.getRecruiterId(),
                        saved.getScheduledAt(),
                        saved.getStatus(),
                        "Interview cancelled"
                )
        );
    }

    @Override
    public List<Interview> getByCandidate(UUID candidateId) {
        return repository.findByCandidateId(candidateId);
    }

    @Override
    public List<Interview> getByRecruiter(UUID recruiterId) {
        return repository.findByRecruiterId(recruiterId);
    }

    @Override
    public List<Interview> getByApplication(UUID applicationId) {
        return repository.findByApplicationId(applicationId);
    }

    @Override
    public List<Interview> getByStatus(InterviewStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Interview getById(UUID interviewId) {
        return repository.findByInterviewId(interviewId)
                .orElseThrow(() ->
                        new InterviewNotFoundException(
                                "Interview not found with id: " + interviewId
                        ));
    }
}