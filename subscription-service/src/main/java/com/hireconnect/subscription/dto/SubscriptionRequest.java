package com.hireconnect.subscription.dto;

import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SubscriptionRequest {

    @NotNull(message = "Recruiter id is required")
    private UUID recruiterId;

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan plan;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;
}