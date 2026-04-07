package com.hireconnect.analytics.service;

import com.hireconnect.analytics.client.ApplicationServiceClient;
import com.hireconnect.analytics.client.JobServiceClient;
import com.hireconnect.analytics.dto.AnalyticsSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImp implements AnalyticsService {

    private final JobServiceClient jobServiceClient;
    private final ApplicationServiceClient applicationServiceClient;

    @Override
    public int getJobViewCount(Long jobId) {
        return jobServiceClient.getJobViewCount(jobId);
    }

    @Override
    public int getAppCountByJob(Long jobId) {
        return applicationServiceClient.getApplicationCountByJob(jobId);
    }

    @Override
    public double getViewToApplyRatio(Long jobId) {
        int views = getJobViewCount(jobId);
        int applications = getAppCountByJob(jobId);

        if (views == 0) {
            return 0.0;
        }

        return (double) applications / views;
    }

    @Override
    public double getTimeToHire(Long jobId) {
        return applicationServiceClient.getAverageTimeToHire(jobId);
    }

    @Override
    public AnalyticsSummary getPipelineStats(Long recruiterId) {

        Long totalJobs = jobServiceClient.getRecruiterJobCount(recruiterId);
        Long totalApplications = applicationServiceClient.getRecruiterApplicationCount(recruiterId);

        Integer shortlisted = applicationServiceClient.getShortlistedCount(recruiterId);
        Integer offered = applicationServiceClient.getOfferedCount(recruiterId);
        Integer rejected = applicationServiceClient.getRejectedCount(recruiterId);

        Double avgTime = applicationServiceClient.getAverageTimeToHire(recruiterId);

        Double ratio = totalJobs == 0 ? 0.0 : (double) totalApplications / totalJobs;

        return new AnalyticsSummary(
                totalJobs,
                totalApplications,
                shortlisted,
                offered,
                rejected,
                avgTime,
                ratio
        );
    }

    @Override
    public AnalyticsSummary getPlatformStats() {

        Long totalJobs = 0L; // later from job-service endpoint
        Long totalApplications = applicationServiceClient.getPlatformApplicationCount();

        Integer shortlisted = applicationServiceClient.getPlatformShortlistedCount();
        Integer offered = applicationServiceClient.getPlatformOfferedCount();
        Integer rejected = applicationServiceClient.getPlatformRejectedCount();

        Double avgTime = applicationServiceClient.getPlatformAverageTimeToHire();

        Double ratio = totalJobs == 0 ? 0.0 : (double) totalApplications / totalJobs;

        return new AnalyticsSummary(
                totalJobs,
                totalApplications,
                shortlisted,
                offered,
                rejected,
                avgTime,
                ratio
        );
    }

    @Override
    public Map<String, Long> getTopJobCategories() {
        return jobServiceClient.getTopCategories();
    }
}