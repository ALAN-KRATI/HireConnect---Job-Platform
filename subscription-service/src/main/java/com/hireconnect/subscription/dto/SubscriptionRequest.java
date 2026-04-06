package com.hireconnect.subscription.dto;

import com.hireconnect.subscription.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequest {

    @NotNull(message = "Recruiter id is required")
    private Integer recruiterId;

    @NotNull(message = "Plan is required")
    private SubscriptionPlan plan;
}