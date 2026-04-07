package com.hireconnect.subscription.service;

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
    public Subscription subscribe(Integer recruiterId, SubscriptionPlan plan) {

        subscriptionRepository.findByRecruiterIdAndStatus(recruiterId, SubscriptionStatus.ACTIVE)
                .ifPresent(subscription -> {
                    throw new SubscriptionAlreadyExistsException(
                            "Recruiter already has an active subscription");
                });

        double amount;

        switch (plan) {
            case FREE:
                amount = 0.0;
                break;
            case PROFESSIONAL:
                amount = 999.0;
                break;
            case ENTERPRISE:
                amount = 2999.0;
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription plan");
        }

        Subscription subscription = Subscription.builder()
                .recruiterId(recruiterId)
                .plan(plan)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .amountPaid(amount)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        generateInvoice(saved);

        return saved;
    }

    @Override
    public Subscription cancelSubscription(Integer subscriptionId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus(SubscriptionStatus.CANCELLED);

        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription renewSubscription(Integer subscriptionId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription renewed = subscriptionRepository.save(subscription);

        generateInvoice(renewed);

        return renewed;
    }

    @Override
    public List<Subscription> getByRecruiter(Integer recruiterId) {
        return subscriptionRepository.findByRecruiterId(recruiterId);
    }

    @Override
    public Invoice generateInvoice(Subscription subscription) {

        Invoice invoice = Invoice.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .recruiterId(subscription.getRecruiterId())
                .amount(subscription.getAmountPaid())
                .paymentDate(LocalDateTime.now())
                .paymentMode(PaymentMode.UPI)
                .transactionId("TXN-" + System.currentTimeMillis())
                .build();

        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getInvoices(Integer recruiterId) {
        return invoiceRepository.findByRecruiterId(recruiterId);
    }
}