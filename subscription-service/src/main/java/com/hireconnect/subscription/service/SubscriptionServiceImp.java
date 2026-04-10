package com.hireconnect.subscription.service;

import com.hireconnect.subscription.dto.InvoiceResponse;
import com.hireconnect.subscription.dto.SubscriptionResponse;
import com.hireconnect.subscription.entity.Invoice;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;
import com.hireconnect.subscription.exception.ResourceNotFoundException;
import com.hireconnect.subscription.exception.SubscriptionAlreadyExistsException;
import com.hireconnect.subscription.repository.InvoiceRepository;
import com.hireconnect.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImp implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public SubscriptionResponse subscribe(
            Integer recruiterId,
            SubscriptionPlan plan,
            PaymentMode paymentMode
    ) {

        subscriptionRepository.findByRecruiterIdAndStatus(
                recruiterId,
                SubscriptionStatus.ACTIVE
        ).ifPresent(subscription -> {
            throw new SubscriptionAlreadyExistsException(
                    "Recruiter already has an active subscription"
            );
        });

        double amount = switch (plan) {
            case FREE -> 0.0;
            case PROFESSIONAL -> 999.0;
            case ENTERPRISE -> 2999.0;
        };

        Subscription subscription = Subscription.builder()
                .recruiterId(recruiterId)
                .plan(plan)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .amountPaid(amount)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        generateInvoice(savedSubscription, paymentMode);

        return mapSubscription(savedSubscription);
    }

    @Override
    public SubscriptionResponse cancelSubscription(Integer subscriptionId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found with id: " + subscriptionId
                ));

        subscription.setStatus(SubscriptionStatus.CANCELLED);

        return mapSubscription(subscriptionRepository.save(subscription));
    }

    @Override
    public SubscriptionResponse renewSubscription(Integer subscriptionId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found with id: " + subscriptionId
                ));

        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription renewedSubscription = subscriptionRepository.save(subscription);

        generateInvoice(renewedSubscription, PaymentMode.UPI);

        return mapSubscription(renewedSubscription);
    }

    @Override
    public List<SubscriptionResponse> getByRecruiter(Integer recruiterId) {

        return subscriptionRepository.findByRecruiterId(recruiterId)
                .stream()
                .map(this::mapSubscription)
                .toList();
    }

    @Override
    public List<InvoiceResponse> getInvoices(Integer recruiterId) {

        return invoiceRepository.findByRecruiterId(recruiterId)
                .stream()
                .map(this::mapInvoice)
                .toList();
    }

    @Override
    public Integer getAllowedJobLimit(Integer recruiterId) {

        Subscription subscription = getActiveSubscription(recruiterId);

        return switch (subscription.getPlan()) {
            case FREE -> 3;
            case PROFESSIONAL -> 20;
            case ENTERPRISE -> Integer.MAX_VALUE;
        };
    }

    @Override
    public boolean canPostMoreJobs(Integer recruiterId) {

        Subscription subscription = getActiveSubscription(recruiterId);

        return subscription != null && subscription.isActive();
    }

    @Override
    public Subscription getActiveSubscription(Integer recruiterId) {

        return subscriptionRepository.findByRecruiterIdAndStatus(
                        recruiterId,
                        SubscriptionStatus.ACTIVE
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active subscription found for recruiter: " + recruiterId
                ));
    }

    private Invoice generateInvoice(
            Subscription subscription,
            PaymentMode paymentMode
    ) {

        Invoice invoice = Invoice.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .recruiterId(subscription.getRecruiterId())
                .amount(subscription.getAmountPaid())
                .paymentDate(LocalDateTime.now())
                .paymentMode(paymentMode)
                .transactionId("TXN-" + System.currentTimeMillis())
                .build();

        return invoiceRepository.save(invoice);
    }

    private SubscriptionResponse mapSubscription(Subscription subscription) {

        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .recruiterId(subscription.getRecruiterId())
                .plan(subscription.getPlan())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .amountPaid(subscription.getAmountPaid())
                .build();
    }

    private InvoiceResponse mapInvoice(Invoice invoice) {

        return InvoiceResponse.builder()
                .invoiceId(invoice.getInvoiceId())
                .subscriptionId(invoice.getSubscriptionId())
                .recruiterId(invoice.getRecruiterId())
                .amount(invoice.getAmount())
                .paymentMode(invoice.getPaymentMode())
                .transactionId(invoice.getTransactionId())
                .paymentDate(invoice.getPaymentDate())
                .build();
    }
}