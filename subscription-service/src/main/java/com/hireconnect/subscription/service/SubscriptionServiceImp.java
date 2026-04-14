package com.hireconnect.subscription.service;

import com.hireconnect.subscription.dto.*;
import com.hireconnect.subscription.entity.Invoice;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.PaymentStatus;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;
import com.hireconnect.subscription.exception.ResourceNotFoundException;
import com.hireconnect.subscription.exception.SubscriptionAlreadyExistsException;
import com.hireconnect.subscription.repository.InvoiceRepository;
import com.hireconnect.subscription.repository.SubscriptionRepository;
import com.hireconnect.subscription.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImp implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final PdfGenerator pdfGenerator;

    @Override
    @Transactional
    public SubscriptionResponse subscribe(
            UUID recruiterId,
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

        double amount = getPlanAmount(plan);

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
    @Transactional
    public SubscriptionResponse cancelSubscription(Integer subscriptionId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found with id: " + subscriptionId
                ));

        subscription.setStatus(SubscriptionStatus.CANCELLED);

        return mapSubscription(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
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
    public List<SubscriptionResponse> getByRecruiter(UUID recruiterId) {

        return subscriptionRepository.findByRecruiterId(recruiterId)
                .stream()
                .map(this::mapSubscription)
                .toList();
    }

    @Override
    public List<InvoiceResponse> getInvoices(UUID recruiterId) {

        return invoiceRepository.findByRecruiterId(recruiterId)
                .stream()
                .map(this::mapInvoice)
                .toList();
    }

    @Override
    public Integer getAllowedJobLimit(UUID recruiterId) {

        Subscription subscription = getActiveSubscription(recruiterId);

        return switch (subscription.getPlan()) {
            case FREE -> 3;
            case PROFESSIONAL -> 20;
            case ENTERPRISE -> Integer.MAX_VALUE;
        };
    }

    @Override
    public boolean canPostMoreJobs(UUID recruiterId) {

        Subscription subscription = getActiveSubscription(recruiterId);

        return subscription != null && subscription.isActive();
    }

    @Override
    public Subscription getActiveSubscription(UUID recruiterId) {

        return subscriptionRepository.findByRecruiterIdAndStatus(
                        recruiterId,
                        SubscriptionStatus.ACTIVE
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active subscription found for recruiter: " + recruiterId
                ));
    }

    @Override
    @Transactional
    public PaymentResponseDTO createPaymentIntent(UUID recruiterId, CreatePaymentRequestDTO request) {
        throw new UnsupportedOperationException("Use StripePaymentService for payment intents");
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(String paymentIntentId) {
        Invoice invoice = invoiceRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for payment: " + paymentIntentId));
        
        invoice.setPaymentStatus(PaymentStatus.COMPLETED);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setTransactionId("TXN-" + System.currentTimeMillis());
        invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public void handlePaymentFailure(String paymentIntentId, String failureMessage) {
        Invoice invoice = invoiceRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for payment: " + paymentIntentId));
        
        invoice.setPaymentStatus(PaymentStatus.FAILED);
        invoice.setFailureReason(failureMessage);
        invoiceRepository.save(invoice);
        
        Subscription subscription = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public byte[] generateInvoicePdf(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        Subscription subscription = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        
        return pdfGenerator.generateInvoicePdf(invoice, subscription, "Recruiter", "recruiter@example.com");
    }

    @Override
    public InvoiceResponseDTO getInvoiceDetails(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        
        Subscription subscription = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        
        return mapInvoiceToDTO(invoice, subscription);
    }

    @Override
    public SubscriptionAnalyticsDTO getAnalytics(UUID recruiterId) {
        List<Subscription> subscriptions = subscriptionRepository.findByRecruiterId(recruiterId);
        List<Invoice> invoices = invoiceRepository.findByRecruiterId(recruiterId);
        
        double totalRevenue = invoices.stream()
                .filter(inv -> inv.getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Invoice::getAmount)
                .sum();
        
        Subscription activeSub = subscriptions.stream()
                .filter(Subscription::isActive)
                .findFirst()
                .orElse(null);
        
        SubscriptionAnalyticsDTO.RecruiterStatsDTO stats = SubscriptionAnalyticsDTO.RecruiterStatsDTO.builder()
                .recruiterId(recruiterId)
                .currentPlan(activeSub != null ? activeSub.getPlan().name() : "NONE")
                .isActive(activeSub != null)
                .daysRemaining(activeSub != null ? 
                    (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), activeSub.getEndDate()) : 0)
                .totalSpent(totalRevenue)
                .totalInvoices(invoices.size())
                .build();
        
        return SubscriptionAnalyticsDTO.builder()
                .totalSubscriptions(subscriptions.size())
                .activeSubscriptions((int) subscriptions.stream().filter(Subscription::isActive).count())
                .expiredSubscriptions((int) subscriptions.stream().filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED).count())
                .cancelledSubscriptions((int) subscriptions.stream().filter(s -> s.getStatus() == SubscriptionStatus.CANCELLED).count())
                .totalRevenue(totalRevenue)
                .recruiterStats(stats)
                .recentInvoices(mapRecentInvoices(invoices))
                .build();
    }

    @Override
    public SubscriptionAnalyticsDTO getAdminAnalytics() {
        List<Subscription> allSubs = subscriptionRepository.findAll();
        List<Invoice> allInvoices = invoiceRepository.findAll();
        
        double totalRevenue = allInvoices.stream()
                .filter(inv -> inv.getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Invoice::getAmount)
                .sum();
        
        java.util.Map<String, Integer> planDist = allSubs.stream()
                .collect(Collectors.groupingBy(s -> s.getPlan().name(), 
                    Collectors.summingInt(s -> 1)));
        
        return SubscriptionAnalyticsDTO.builder()
                .totalSubscriptions(allSubs.size())
                .activeSubscriptions((int) allSubs.stream().filter(Subscription::isActive).count())
                .expiredSubscriptions((int) allSubs.stream().filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED).count())
                .cancelledSubscriptions((int) allSubs.stream().filter(s -> s.getStatus() == SubscriptionStatus.CANCELLED).count())
                .totalRevenue(totalRevenue)
                .planDistribution(planDist)
                .build();
    }

    private List<SubscriptionAnalyticsDTO.RecentInvoiceDTO> mapRecentInvoices(List<Invoice> invoices) {
        return invoices.stream()
                .sorted((a, b) -> b.getPaymentDate().compareTo(a.getPaymentDate()))
                .limit(5)
                .map(inv -> SubscriptionAnalyticsDTO.RecentInvoiceDTO.builder()
                        .invoiceId(inv.getInvoiceId())
                        .planName("Subscription")
                        .amount(inv.getAmount())
                        .status(inv.getPaymentStatus() != null ? inv.getPaymentStatus().name() : "UNKNOWN")
                        .date(inv.getPaymentDate() != null ? inv.getPaymentDate().toString() : "N/A")
                        .build())
                .toList();
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
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionId("TXN-" + System.currentTimeMillis())
                .build();

        return invoiceRepository.save(invoice);
    }

    private double getPlanAmount(SubscriptionPlan plan) {
        return switch (plan) {
            case FREE -> 0.0;
            case PROFESSIONAL -> 999.0;
            case ENTERPRISE -> 2999.0;
        };
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

    private InvoiceResponseDTO mapInvoiceToDTO(Invoice invoice, Subscription subscription) {
        return InvoiceResponseDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .subscriptionId(invoice.getSubscriptionId())
                .planName(subscription.getPlan().name())
                .amount(invoice.getAmount())
                .currency("INR")
                .paymentDate(invoice.getPaymentDate())
                .paymentMode(invoice.getPaymentMode())
                .paymentStatus(invoice.getPaymentStatus())
                .transactionId(invoice.getTransactionId())
                .stripePaymentIntentId(invoice.getStripePaymentIntentId())
                .invoiceUrl(invoice.getInvoiceUrl())
                .receiptUrl(invoice.getReceiptUrl())
                .pdfDownloadUrl("/subscriptions/invoices/" + invoice.getInvoiceId() + "/download")
                .build();
    }
}
