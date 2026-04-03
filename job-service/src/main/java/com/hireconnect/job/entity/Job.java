package com.hireconnect.job.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.enums.JobType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(nullable = false)
    private String location;

    private double minSalary;

    private double maxSalary;

    @Column(length = 5000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skills")
    private List<String> skills;

    private int experienceRequired;

    @Column(nullable = false)
    private Long postedBy;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private LocalDateTime postedAt;

}
