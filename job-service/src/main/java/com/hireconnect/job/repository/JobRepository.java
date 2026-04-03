package com.hireconnect.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByCategoryIgnoreCase(String category);

    List<Job> findByLocationIgnoreCase(String location); 

    List<Job> findByPostedBy(Long postedBy);

    List<Job> findByStatus(JobStatus status);

    Optional<Job> findByTitle(String title);

}
