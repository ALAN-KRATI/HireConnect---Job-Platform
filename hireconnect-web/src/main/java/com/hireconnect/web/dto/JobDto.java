package com.hireconnect.web.dto;

import com.hireconnect.web.enums.JobStatus;
import com.hireconnect.web.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {

    private UUID recruiterId;
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
    private UUID postedBy;
    private JobStatus status;
    private LocalDateTime postedAt;
    private String description;
}