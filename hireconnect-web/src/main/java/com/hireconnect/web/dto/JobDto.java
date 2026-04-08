package com.hireconnect.web.dto;

import com.hireconnect.web.enums.EmploymentType;
import com.hireconnect.web.enums.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {

    private Long id;
    private Long recruiterId;

    private String title;
    private String description;
    private String companyName;
    private String location;

    private Double salary;
    private String experience;
    private String category;
    private EmploymentType employmentType;

    private JobStatus status;
}