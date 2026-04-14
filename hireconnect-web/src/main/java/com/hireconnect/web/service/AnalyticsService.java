package com.hireconnect.web.service;

import com.hireconnect.web.dto.AnalyticsDto;

import java.util.UUID;

public interface AnalyticsService {

    AnalyticsDto getRecruiterAnalytics(UUID recruiterId);

    AnalyticsDto getPlatformAnalytics();

    byte[] exportPlatformReport();
}