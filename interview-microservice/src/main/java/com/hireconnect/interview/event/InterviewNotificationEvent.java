package com.hireconnect.interview.event;

import java.io.Serializable;

public class InterviewNotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long applicationId;
    private String message;

    public InterviewNotificationEvent() {
    }

    public InterviewNotificationEvent(Long applicationId, String message) {
        this.applicationId = applicationId;
        this.message = message;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}