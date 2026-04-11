package com.hireconnect.web.dto;

import com.hireconnect.web.enums.JobStatus;
import com.hireconnect.web.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {

    private Long recruiterId;
    private String companyName;
    private Long jobId;
    private String title;
    private String category;
    private JobType type;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private List<String> skills;
    private Integer experienceRequired;
    private Long postedBy;
    private JobStatus status;
    private LocalDateTime postedAt;
    private String description;
}