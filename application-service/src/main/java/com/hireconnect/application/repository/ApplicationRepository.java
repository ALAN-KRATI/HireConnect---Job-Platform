package com.hireconnect.application.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hireconnect.application.entity.Application;
import com.hireconnect.application.enums.ApplicationStatus;


public interface ApplicationRepository extends JpaRepository<Application, Long>{
    List<Application> findByCandidateId(Long id);

    List<Application> findByJobId(Long id);

    List<Application> findByStatus(ApplicationStatus status);

    List<Application> findByAppliedAtBetween(LocalDate start, LocalDate end);

    int countByJobId(Long id);

    Optional<Application> findFirstByJobIdAndCandidateId(Long id, Long candidateId);
}
