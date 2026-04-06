package com.hireconnect.subscription.dto;

import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SubscriptionResponse {

    private Integer subscriptionId;
    private Integer recruiterId;
    private SubscriptionPlan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private Double amountPaid;
}