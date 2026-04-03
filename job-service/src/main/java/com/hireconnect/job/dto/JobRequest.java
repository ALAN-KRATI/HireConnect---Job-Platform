package com.hireconnect.job.dto;

import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.enums.JobType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobRequest {
    private String title;
    private String category;
    private JobType type;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private List<String> skills;
    private Integer experienceRequired;
    private JobStatus status;
    private Long postedBy;
    private String description;
    private LocalDateTime postedAt;
}