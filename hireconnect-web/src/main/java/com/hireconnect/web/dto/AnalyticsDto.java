package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsDto {

    private Long totalJobs;
    private Long totalApplications;
    private Long shortlistedCount;
    private Long offeredCount;
    private Long rejectedCount;

    private Double avgTimeToHireDays;
    private Double viewToApplyRatio;
}