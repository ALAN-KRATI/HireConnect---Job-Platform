package com.hireconnect.web.dto;

import com.hireconnect.web.enums.PaymentMethod;
import com.hireconnect.web.enums.SubscriptionPlan;
import com.hireconnect.web.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

    private Long subscriptionId;
    private Long recruiterId;

    private String recruiterName;

    private SubscriptionPlan planName;
    private SubscriptionStatus status;

    private Double monthlyPrice;
    private PaymentMethod paymentMethod;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean autoRenew;
}