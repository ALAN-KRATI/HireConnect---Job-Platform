package com.hireconnect.application.repository;

import com.hireconnect.application.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {

    List<SavedJob> findByCandidateId(UUID candidateId);

    Optional<SavedJob> findByCandidateIdAndJobId(UUID candidateId, UUID jobId);
}