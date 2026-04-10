package com.hireconnect.job.repository;

import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByCategoryIgnoreCase(String category);

    List<Job> findByLocationIgnoreCase(String location);

    List<Job> findByPostedBy(Long postedBy);

    List<Job> findByPostedByAndStatus(Long postedBy, JobStatus status);

    List<Job> findByStatus(JobStatus status);

    long countByPostedBy(Long postedBy);

    long countByCategoryIgnoreCase(String category);

    Long countByPostedByAndStatus(Long postedBy, JobStatus status);
}