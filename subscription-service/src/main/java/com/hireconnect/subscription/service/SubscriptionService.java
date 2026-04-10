package com.hireconnect.subscription.service;

import com.hireconnect.subscription.dto.InvoiceResponse;
import com.hireconnect.subscription.dto.SubscriptionResponse;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.SubscriptionPlan;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse subscribe(
            Integer recruiterId,
            SubscriptionPlan plan,
            PaymentMode paymentMode
    );

    SubscriptionResponse cancelSubscription(Integer subscriptionId);

    SubscriptionResponse renewSubscription(Integer subscriptionId);

    List<SubscriptionResponse> getByRecruiter(Integer recruiterId);

    List<InvoiceResponse> getInvoices(Integer recruiterId);

    Integer getAllowedJobLimit(Integer recruiterId);

    boolean canPostMoreJobs(Integer recruiterId);

    Subscription getActiveSubscription(Integer recruiterId);
}