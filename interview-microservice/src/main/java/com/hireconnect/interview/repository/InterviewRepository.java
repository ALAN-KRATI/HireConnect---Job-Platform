package com.hireconnect.interview.repository;

import com.hireconnect.interview.entity.Interview;
import com.hireconnect.interview.enums.InterviewStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);

    List<Interview> findByStatus(InterviewStatus status);

    List<Interview> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Interview> findByInterviewId(Long interviewId);

    void deleteByInterviewId(Long interviewId);

    boolean existsByScheduledAtAndStatusNot(
        LocalDateTime scheduledAt,
        InterviewStatus status);
}