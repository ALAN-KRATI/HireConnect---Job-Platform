package com.hireconnect.web.service;

import com.hireconnect.web.dto.AnalyticsDto;

public interface AnalyticsService {

    AnalyticsDto getRecruiterAnalytics(Long recruiterId);

    AnalyticsDto getPlatformAnalytics();

    byte[] exportPlatformReport();
}