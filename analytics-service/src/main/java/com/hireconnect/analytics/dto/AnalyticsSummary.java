package com.hireconnect.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummary {

    private Long totalJobs;
    private Long totalApplications;
    private Integer shortlistedCount;
    private Integer offeredCount;
    private Integer rejectedCount;
    private Double avgTimeToHireDays;
    private Double viewToApplyRatio;
}