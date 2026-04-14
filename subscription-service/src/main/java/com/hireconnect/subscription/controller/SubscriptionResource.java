package com.hireconnect.subscription.controller;

import com.hireconnect.subscription.dto.*;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.service.StripePaymentService;
import com.hireconnect.subscription.service.SubscriptionService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionResource {

    private final SubscriptionService subscriptionService;
    private final StripePaymentService stripePaymentService;

    @GetMapping("/plans")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<String>> getAvailablePlans() {
        return ResponseEntity.ok(List.of("FREE", "PROFESSIONAL", "ENTERPRISE"));
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(
            @RequestParam UUID recruiterId
    ) {
        List<SubscriptionResponse> subs = subscriptionService.getByRecruiter(recruiterId);
        return ResponseEntity.ok(subs.stream()
                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                .findFirst()
                .orElse(null));
    }

    @PostMapping("/upgrade")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionResponse> upgradePlan(
            @RequestParam UUID recruiterId,
            @RequestParam String plan
    ) {
        return ResponseEntity.ok(
                subscriptionService.subscribe(
                        recruiterId,
                        SubscriptionPlan.valueOf(plan),
                        PaymentMode.CARD
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @Valid @RequestBody SubscriptionRequest request
    ) {
        return ResponseEntity.ok(
                subscriptionService.subscribe(
                        request.getRecruiterId(),
                        request.getPlan(),
                        request.getPaymentMode()
                )
        );
    }

    @PutMapping("/{subscriptionId}/cancel")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionResponse> cancel(
            @PathVariable Integer subscriptionId
    ) {
        return ResponseEntity.ok(
                subscriptionService.cancelSubscription(subscriptionId)
        );
    }

    @PutMapping("/{subscriptionId}/renew")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionResponse> renew(
            @PathVariable Integer subscriptionId
    ) {
        return ResponseEntity.ok(
                subscriptionService.renewSubscription(subscriptionId)
        );
    }

    @GetMapping("/recruiter/{recruiterId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<SubscriptionResponse>> getByRecruiter(
            @PathVariable UUID recruiterId
    ) {
        return ResponseEntity.ok(subscriptionService.getByRecruiter(recruiterId));
    }

    @GetMapping("/invoices/{recruiterId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @PathVariable UUID recruiterId
    ) {
        return ResponseEntity.ok(subscriptionService.getInvoices(recruiterId));
    }

    @GetMapping("/recruiter/{recruiterId}/job-limit")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Integer> getJobLimit(
            @PathVariable UUID recruiterId
    ) {
        return ResponseEntity.ok(
                subscriptionService.getAllowedJobLimit(recruiterId)
        );
    }

    @GetMapping("/recruiter/{recruiterId}/can-post")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Boolean> canPostMoreJobs(
            @PathVariable UUID recruiterId
    ) {
        return ResponseEntity.ok(
                subscriptionService.canPostMoreJobs(recruiterId)
        );
    }

    @PostMapping("/payment/checkout")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PaymentResponseDTO> createCheckoutSession(
            @RequestParam UUID recruiterId,
            @Valid @RequestBody CreatePaymentRequestDTO request
    ) throws StripeException {
        return ResponseEntity.ok(stripePaymentService.createCheckoutSession(recruiterId, request));
    }

    @PostMapping("/payment/intent")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PaymentResponseDTO> createPaymentIntent(
            @RequestParam UUID recruiterId,
            @Valid @RequestBody CreatePaymentRequestDTO request
    ) throws StripeException {
        return ResponseEntity.ok(stripePaymentService.createPaymentIntent(recruiterId, request));
    }

    @GetMapping("/stripe-config")
    public ResponseEntity<java.util.Map<String, String>> getStripeConfig() {
        return ResponseEntity.ok(java.util.Map.of("publishableKey", stripePaymentService.getPublishableKey()));
    }

    @GetMapping("/invoices/{invoiceId}/download")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer invoiceId) {
        byte[] pdfBytes = subscriptionService.generateInvoicePdf(invoiceId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoiceId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/invoices/{invoiceId}/details")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceDetails(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(subscriptionService.getInvoiceDetails(invoiceId));
    }

    @GetMapping("/analytics/recruiter/{recruiterId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionAnalyticsDTO> getRecruiterAnalytics(@PathVariable UUID recruiterId) {
        return ResponseEntity.ok(stripePaymentService.getAnalytics(recruiterId));
    }

    @GetMapping("/analytics/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionAnalyticsDTO> getAdminAnalytics() {
        return ResponseEntity.ok(stripePaymentService.getAdminAnalytics());
    }
}