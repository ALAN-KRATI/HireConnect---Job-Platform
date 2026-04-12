package com.hireconnect.subscription.controller;

import com.hireconnect.subscription.dto.InvoiceResponse;
import com.hireconnect.subscription.dto.SubscriptionRequest;
import com.hireconnect.subscription.dto.SubscriptionResponse;
import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionResource {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<String>> getAvailablePlans() {
        return ResponseEntity.ok(List.of("FREE", "PROFESSIONAL", "ENTERPRISE"));
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(
            @RequestParam Integer recruiterId
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
            @RequestParam Integer recruiterId,
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
            @PathVariable Integer recruiterId
    ) {
        return ResponseEntity.ok(subscriptionService.getByRecruiter(recruiterId));
    }

    @GetMapping("/invoices/{recruiterId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @PathVariable Integer recruiterId
    ) {
        return ResponseEntity.ok(subscriptionService.getInvoices(recruiterId));
    }

    @GetMapping("/recruiter/{recruiterId}/job-limit")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Integer> getJobLimit(
            @PathVariable Integer recruiterId
    ) {
        return ResponseEntity.ok(
                subscriptionService.getAllowedJobLimit(recruiterId)
        );
    }

    @GetMapping("/recruiter/{recruiterId}/can-post")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Boolean> canPostMoreJobs(
            @PathVariable Integer recruiterId
    ) {
        return ResponseEntity.ok(
                subscriptionService.canPostMoreJobs(recruiterId)
        );
    }
}