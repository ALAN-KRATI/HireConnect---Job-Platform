package com.hireconnect.subscription.controller;

import com.hireconnect.subscription.service.StripePaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final StripePaymentService stripePaymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        if (sigHeader == null) {
            log.warn("Missing Stripe-Signature header");
            return ResponseEntity.badRequest().body("Missing signature");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        log.info("Received Stripe webhook event: {}", event.getType());

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                case "invoice.payment_succeeded":
                    log.info("Invoice payment succeeded: {}", event.getId());
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing failed");
        }

        return ResponseEntity.ok("Processed");
    }

    private void handlePaymentIntentSucceeded(Event event) throws Exception {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        
        log.info("Payment succeeded: {}", paymentIntent.getId());
        stripePaymentService.handlePaymentSuccess(paymentIntent.getId());
    }

    private void handlePaymentIntentFailed(Event event) throws Exception {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        
        String failureMessage = paymentIntent.getLastPaymentError() != null ? 
            paymentIntent.getLastPaymentError().getMessage() : "Unknown error";
        
        log.error("Payment failed: {} - {}", paymentIntent.getId(), failureMessage);
        stripePaymentService.handlePaymentFailure(paymentIntent.getId(), failureMessage);
    }

    private void handleCheckoutSessionCompleted(Event event) throws Exception {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
        Session session = (Session) stripeObject;
        
        log.info("Checkout session completed: {}", session.getId());
        stripePaymentService.handleCheckoutCompleted(session.getId());
    }
}
