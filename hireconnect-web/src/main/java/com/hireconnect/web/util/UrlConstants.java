package com.hireconnect.web.util;

public final class UrlConstants {

    private static final String API_GATEWAY = "http://api-gateway:8080";

    public static final String AUTH_SERVICE = API_GATEWAY + "/auth";
    public static final String PROFILE_SERVICE = API_GATEWAY + "/profiles";
    public static final String JOB_SERVICE = API_GATEWAY + "/jobs";
    public static final String APPLICATION_SERVICE = API_GATEWAY + "/applications";
    public static final String INTERVIEW_SERVICE = API_GATEWAY + "/interviews";
    public static final String NOTIFICATION_SERVICE = API_GATEWAY + "/notifications";
    public static final String SUBSCRIPTION_SERVICE = API_GATEWAY + "/subscriptions";
    public static final String ANALYTICS_SERVICE = API_GATEWAY + "/analytics";

    private UrlConstants() {
    }
}