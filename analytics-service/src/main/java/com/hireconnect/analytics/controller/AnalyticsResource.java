package com.hireconnect.analytics.controller;

import com.hireconnect.analytics.dto.AnalyticsSummary;
import com.hireconnect.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsResource {

    private final AnalyticsService analyticsService;

    @GetMapping("/recruiter/{id}")
    public ResponseEntity<AnalyticsSummary> getRecruiterStats(@PathVariable Long id) {
        return ResponseEntity.ok(analyticsService.getPipelineStats(id));
    }

    @GetMapping("/admin")
    public ResponseEntity<AnalyticsSummary> getPlatformStats() {
        return ResponseEntity.ok(analyticsService.getPlatformStats());
    }

    @GetMapping("/job/{jobId}/views")
    public ResponseEntity<Integer> getJobViewCount(@PathVariable Long jobId) {
        return ResponseEntity.ok(analyticsService.getJobViewCount(jobId));
    }

    @GetMapping("/job/{jobId}/applications")
    public ResponseEntity<Integer> getApplicationCount(@PathVariable Long jobId) {
        return ResponseEntity.ok(analyticsService.getAppCountByJob(jobId));
    }

    @GetMapping("/job/{jobId}/ratio")
    public ResponseEntity<Double> getViewToApplyRatio(@PathVariable Long jobId) {
        return ResponseEntity.ok(analyticsService.getViewToApplyRatio(jobId));
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Long>> getTopCategories() {
        return ResponseEntity.ok(analyticsService.getTopJobCategories());
    }
}