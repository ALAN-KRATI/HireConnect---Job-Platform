package com.hireconnect.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "job-service")
public interface JobServiceClient {

    @GetMapping("/jobs/recruiter/{recruiterId}/count")
    Long getRecruiterJobCount(@PathVariable Long recruiterId);

    @GetMapping("/jobs/{jobId}/views")
    Integer getJobViewCount(@PathVariable Long jobId);

    @GetMapping("/jobs/categories/top")
    Map<String, Long> getTopCategories();
}