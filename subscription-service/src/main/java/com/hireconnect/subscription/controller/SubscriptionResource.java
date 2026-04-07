package com.hireconnect.subscription.controller;

import com.hireconnect.subscription.dto.SubscriptionRequest;
import com.hireconnect.subscription.entity.Invoice;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionResource {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Subscription> subscribe(
            @Valid @RequestBody SubscriptionRequest request) {

        return ResponseEntity.ok(
                subscriptionService.subscribe(
                        request.getRecruiterId(),
                        request.getPlan()
                )
        );
    }

    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<Subscription> cancel(@PathVariable Integer subscriptionId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(subscriptionId));
    }

    @PutMapping("/{subscriptionId}/renew")
    public ResponseEntity<Subscription> renew(@PathVariable Integer subscriptionId) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(subscriptionId));
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<Subscription>> getByRecruiter(@PathVariable Integer recruiterId) {
        return ResponseEntity.ok(subscriptionService.getByRecruiter(recruiterId));
    }

    @GetMapping("/invoices/{recruiterId}")
    public ResponseEntity<List<Invoice>> getInvoices(@PathVariable Integer recruiterId) {
        return ResponseEntity.ok(subscriptionService.getInvoices(recruiterId));
    }
}