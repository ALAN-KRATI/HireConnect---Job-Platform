package com.hireconnect.subscription.service;

import com.hireconnect.subscription.dto.CreatePaymentRequestDTO;
import com.hireconnect.subscription.dto.PaymentResponseDTO;
import com.hireconnect.subscription.dto.SubscriptionAnalyticsDTO;
import com.hireconnect.subscription.entity.Invoice;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.PaymentStatus;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;
import com.hireconnect.subscription.exception.ResourceNotFoundException;
import com.hireconnect.subscription.repository.InvoiceRepository;
import com.hireconnect.subscription.repository.SubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripePaymentService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

    @Value("${stripe.publishable.key}")
    private String publishableKey;

    @Value("${stripe.success.url}")
    private String defaultSuccessUrl;

    @Value("${stripe.cancel.url}")
    private String defaultCancelUrl;

    public PaymentResponseDTO createCheckoutSession(
            UUID recruiterId,
            CreatePaymentRequestDTO request
    ) throws StripeException {

        log.info("Creating checkout session for recruiter {} with plan {}",
                recruiterId, request.getPlan());

        SubscriptionPlan plan = request.getPlan();
        long amount = getPlanAmount(plan);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(
                        request.getSuccessUrl() != null
                                ? request.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}"
                                : defaultSuccessUrl + "?session_id={CHECKOUT_SESSION_ID}"
                )
                .setCancelUrl(
                        request.getCancelUrl() != null
                                ? request.getCancelUrl()
                                : defaultCancelUrl
                )
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("HireConnect " + plan.name() + " Plan")
                                                                .setDescription("Recruiter subscription - " + plan.name())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("recruiterId", recruiterId.toString())
                .putMetadata("plan", plan.name())
                .build();

        Session session = Session.create(params);

        Subscription subscription = subscriptionRepository
                .findByRecruiterId(recruiterId)
                .stream()
                .findFirst()
                .orElse(
                        Subscription.builder()
                                .recruiterId(recruiterId)
                                .build()
                );

        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setAmountPaid(amount / 100.0);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        Invoice invoice = Invoice.builder()
                .subscriptionId(savedSubscription.getSubscriptionId())
                .recruiterId(recruiterId)
                .amount(amount / 100.0)
                .paymentMode(PaymentMode.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .stripeCheckoutSessionId(session.getId())
                .planType(plan)
                .build();

        invoiceRepository.save(invoice);

        return PaymentResponseDTO.builder()
                .checkoutSessionId(session.getId())
                .checkoutUrl(session.getUrl())
                .amount(amount / 100.0)
                .currency("inr")
                .status("PENDING")
                .message("Checkout session created successfully")
                .build();
    }

    public PaymentResponseDTO createPaymentIntent(
            UUID recruiterId,
            CreatePaymentRequestDTO request
    ) throws StripeException {

        log.info("Creating payment intent for recruiter {} with plan {}",
                recruiterId, request.getPlan());

        SubscriptionPlan plan = request.getPlan();
        long amount = getPlanAmount(plan);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("inr")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("recruiterId", recruiterId.toString())
                .putMetadata("plan", plan.name())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Subscription subscription = subscriptionRepository
                .findByRecruiterId(recruiterId)
                .stream()
                .findFirst()
                .orElse(
                        Subscription.builder()
                                .recruiterId(recruiterId)
                                .build()
                );

        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setAmountPaid(amount / 100.0);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        Invoice invoice = Invoice.builder()
                .subscriptionId(savedSubscription.getSubscriptionId())
                .recruiterId(recruiterId)
                .amount(amount / 100.0)
                .paymentMode(PaymentMode.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .stripePaymentIntentId(paymentIntent.getId())
                .planType(plan)
                .build();

        invoiceRepository.save(invoice);

        return PaymentResponseDTO.builder()
                .clientSecret(paymentIntent.getClientSecret())
                .paymentIntentId(paymentIntent.getId())
                .amount(amount / 100.0)
                .currency("inr")
                .status("PENDING")
                .message("Payment intent created successfully")
                .build();
    }

    @Transactional
    public void handlePaymentSuccess(String paymentIntentId) {
        log.info("Handling successful payment: {}", paymentIntentId);

        Invoice invoice = invoiceRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found for payment intent: " + paymentIntentId
                        )
                );

        invoice.setPaymentStatus(PaymentStatus.COMPLETED);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setTransactionId("TXN-" + System.currentTimeMillis());

        invoiceRepository.save(invoice);

        Subscription subscription = subscriptionRepository
                .findById(invoice.getSubscriptionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found: " + invoice.getSubscriptionId()
                        )
                );

        subscription.setPlan(invoice.getPlanType());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setAmountPaid(invoice.getAmount());

        subscriptionRepository.save(subscription);

        log.info(
                "Recruiter {} upgraded successfully to {}",
                invoice.getRecruiterId(),
                invoice.getPlanType()
        );
    }

    @Transactional
    public void handlePaymentFailure(String paymentIntentId, String failureMessage) {
        log.error("Payment failed: {} reason: {}", paymentIntentId, failureMessage);

        Invoice invoice = invoiceRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElse(null);

        if (invoice == null) {
            return;
        }

        invoice.setPaymentStatus(PaymentStatus.FAILED);
        invoice.setFailureReason(failureMessage);
        invoiceRepository.save(invoice);

        Subscription subscription = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElse(null);

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscriptionRepository.save(subscription);
        }
    }

    @Transactional
    public void handleCheckoutCompleted(String sessionId) throws StripeException {
        log.info("Handling checkout session completion: {}", sessionId);

        Session session = Session.retrieve(sessionId);

        if (!"paid".equals(session.getPaymentStatus())) {
            return;
        }

        Invoice invoice = invoiceRepository.findByStripeCheckoutSessionId(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found for session: " + sessionId
                        )
                );

        invoice.setPaymentStatus(PaymentStatus.COMPLETED);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setTransactionId(session.getPaymentIntent());
        invoice.setReceiptUrl(session.getUrl());

        invoiceRepository.save(invoice);

        Subscription subscription = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subscription not found: " + invoice.getSubscriptionId()
                        )
                );

        subscription.setPlan(invoice.getPlanType());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setAmountPaid(invoice.getAmount());

        subscriptionRepository.save(subscription);
    }

    private long getPlanAmount(SubscriptionPlan plan) {
        return switch (plan) {
            case FREE -> 0L;
            case PROFESSIONAL -> 99900L;
            case ENTERPRISE -> 299900L;
        };
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public SubscriptionAnalyticsDTO getAnalytics(UUID recruiterId) {

        List<Subscription> subscriptions = subscriptionRepository.findByRecruiterId(recruiterId);
        List<Invoice> invoices = invoiceRepository.findByRecruiterId(recruiterId);

        Subscription activeSubscription = subscriptions.stream()
                .filter(Subscription::isActive)
                .findFirst()
                .orElse(null);

        double totalSpent = invoices.stream()
                .filter(i -> i.getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Invoice::getAmount)
                .sum();

        SubscriptionAnalyticsDTO.RecruiterStatsDTO stats =
                SubscriptionAnalyticsDTO.RecruiterStatsDTO.builder()
                        .recruiterId(recruiterId)
                        .currentPlan(
                                activeSubscription != null
                                        ? activeSubscription.getPlan().name()
                                        : "FREE"
                        )
                        .isActive(activeSubscription != null)
                        .daysRemaining(
                                activeSubscription != null
                                        ? (int) ChronoUnit.DAYS.between(
                                                LocalDate.now(),
                                                activeSubscription.getEndDate()
                                        )
                                        : 0
                        )
                        .totalSpent(totalSpent)
                        .totalInvoices(invoices.size())
                        .build();

        return SubscriptionAnalyticsDTO.builder()
                .totalSubscriptions(subscriptions.size())
                .activeSubscriptions((int) subscriptions.stream().filter(Subscription::isActive).count())
                .expiredSubscriptions((int) subscriptions.stream()
                        .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
                        .count())
                .cancelledSubscriptions((int) subscriptions.stream()
                        .filter(s -> s.getStatus() == SubscriptionStatus.CANCELLED)
                        .count())
                .totalRevenue(totalSpent)
                .recruiterStats(stats)
                .build();
    }

    public SubscriptionAnalyticsDTO getAdminAnalytics() {

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        List<Invoice> allInvoices = invoiceRepository.findAll();

        Map<String, Integer> planDistribution = allSubscriptions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getPlan().name(),
                        Collectors.summingInt(s -> 1)
                ));

        double totalRevenue = allInvoices.stream()
                .filter(i -> i.getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Invoice::getAmount)
                .sum();

        return SubscriptionAnalyticsDTO.builder()
                .totalSubscriptions(allSubscriptions.size())
                .activeSubscriptions((int) allSubscriptions.stream().filter(Subscription::isActive).count())
                .expiredSubscriptions((int) allSubscriptions.stream()
                        .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
                        .count())
                .cancelledSubscriptions((int) allSubscriptions.stream()
                        .filter(s -> s.getStatus() == SubscriptionStatus.CANCELLED)
                        .count())
                .totalRevenue(totalRevenue)
                .planDistribution(planDistribution)
                .build();
    }
}