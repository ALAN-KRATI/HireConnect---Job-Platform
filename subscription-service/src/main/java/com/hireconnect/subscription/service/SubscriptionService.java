package com.hireconnect.subscription.service;

import com.hireconnect.subscription.dto.*;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.SubscriptionPlan;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    SubscriptionResponse subscribe(
            UUID recruiterId,
            SubscriptionPlan plan,
            PaymentMode paymentMode
    );

    SubscriptionResponse cancelSubscription(Integer subscriptionId);

    SubscriptionResponse renewSubscription(Integer subscriptionId);

    List<SubscriptionResponse> getByRecruiter(UUID recruiterId);

    List<InvoiceResponse> getInvoices(UUID recruiterId);

    Integer getAllowedJobLimit(UUID recruiterId);

    boolean canPostMoreJobs(UUID recruiterId);

    Subscription getActiveSubscription(UUID recruiterId);

    PaymentResponseDTO createPaymentIntent(UUID recruiterId, CreatePaymentRequestDTO request);

    void handlePaymentSuccess(String paymentIntentId);

    void handlePaymentFailure(String paymentIntentId, String failureMessage);

    byte[] generateInvoicePdf(Integer invoiceId);

    InvoiceResponseDTO getInvoiceDetails(Integer invoiceId);

    SubscriptionAnalyticsDTO getAnalytics(UUID recruiterId);

    SubscriptionAnalyticsDTO getAdminAnalytics();
}
