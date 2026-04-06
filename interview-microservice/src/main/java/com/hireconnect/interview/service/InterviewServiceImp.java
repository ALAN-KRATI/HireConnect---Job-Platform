package com.hireconnect.interview.service;

import com.hireconnect.interview.entity.Interview;
import com.hireconnect.interview.enums.InterviewStatus;
import com.hireconnect.interview.event.InterviewNotificationEvent;
import com.hireconnect.interview.exception.InterviewNotFoundException;
import com.hireconnect.interview.repository.InterviewRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewServiceImp implements InterviewService {

    private final InterviewRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public InterviewServiceImp(InterviewRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public Interview scheduleInterview(Interview interview) {
        if (repository.existsByScheduledAtAndStatusNot(interview.getScheduledAt(), InterviewStatus.CANCELLED)){
            throw new IllegalStateException(
                    "An interview is already scheduled for this time slot");
        }

        interview.setStatus(InterviewStatus.SCHEDULED);

        Interview saved = repository.save(interview);

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "interview.scheduled",
                new InterviewNotificationEvent(saved.getApplicationId(), "Interview scheduled for " + saved.getScheduledAt())
        );

        return saved;
    }

    @Override
    public String confirmInterview(Long interviewId) {

        Interview interview = repository.findByInterviewId(interviewId)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        interview.setStatus(InterviewStatus.CONFIRMED);
        repository.save(interview);

        return "Interview confirmed successfully";
    }

    @Override
    public Interview rescheduleInterview(Long interviewId,
                                         LocalDateTime newDateTime) {

        Interview interview = repository.findByInterviewId(interviewId)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        interview.setScheduledAt(newDateTime);
        interview.setStatus(InterviewStatus.RESCHEDULED);

        return repository.save(interview);
    }

    @Override
    public String cancelInterview(Long interviewId) {

        Interview interview = repository.findByInterviewId(interviewId)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        interview.setStatus(InterviewStatus.CANCELLED);
        repository.save(interview);

        return "Interview cancelled successfully";
    }

    @Override
    public List<Interview> getByApplication(Long applicationId) {
        return repository.findByApplicationId(applicationId);
    }

    @Override
    public List<Interview> getByStatus(InterviewStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Optional<Interview> getById(Long interviewId) {
        return repository.findByInterviewId(interviewId);
    }
}