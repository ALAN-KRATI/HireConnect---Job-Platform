package com.hireconnect.web.dto;

import com.hireconnect.web.enums.PaymentMethod;
import com.hireconnect.web.enums.SubscriptionPlan;
import com.hireconnect.web.enums.SubscriptionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {

    private Long id;
    private Long recruiterId;

    private SubscriptionPlan planName;       // Free, Professional, Enterprise
    private SubscriptionStatus status;         // Active, Expired, Cancelled

    private Double monthlyPrice;
    private PaymentMethod paymentMethod;
}