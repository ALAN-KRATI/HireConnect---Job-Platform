package com.hireconnect.analytics.controller;

import com.hireconnect.analytics.dto.AnalyticsSummary;
import com.hireconnect.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsResource {

        private final AnalyticsService analyticsService;

        @GetMapping("/recruiter/{recruiterId}/dashboard")
        @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
        public ResponseEntity<AnalyticsSummary> getRecruiterDashboard(
                        @PathVariable Long recruiterId) {

                return ResponseEntity.ok(
                                analyticsService.getPipelineStats(recruiterId));
        }

        @GetMapping("/platform/summary")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<AnalyticsSummary> getPlatformSummary() {

                return ResponseEntity.ok(
                                analyticsService.getPlatformStats());
        }

        @GetMapping("/job/{jobId}/views")
        @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
        public ResponseEntity<Integer> getJobViewCount(
                        @PathVariable Long jobId) {

                return ResponseEntity.ok(
                                analyticsService.getJobViewCount(jobId));
        }

        @GetMapping("/job/{jobId}/applications")
        @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
        public ResponseEntity<Integer> getApplicationCount(
                        @PathVariable Long jobId) {

                return ResponseEntity.ok(
                                analyticsService.getAppCountByJob(jobId));
        }

        @GetMapping("/job/{jobId}/view-to-apply-ratio")
        @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
        public ResponseEntity<Double> getViewToApplyRatio(
                        @PathVariable Long jobId) {

                return ResponseEntity.ok(
                                analyticsService.getViewToApplyRatio(jobId));
        }

        @GetMapping("/recruiter/{recruiterId}/time-to-hire")
        @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
        public ResponseEntity<Double> getAverageTimeToHire(
                        @PathVariable Long recruiterId) {

                return ResponseEntity.ok(
                                analyticsService.getTimeToHire(recruiterId));
        }

        @GetMapping("/categories/top")
        @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
        public ResponseEntity<Map<String, Long>> getTopCategories() {

                return ResponseEntity.ok(
                                analyticsService.getTopJobCategories());
        }

        
}