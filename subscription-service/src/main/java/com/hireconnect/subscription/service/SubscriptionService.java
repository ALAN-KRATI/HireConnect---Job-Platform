package com.hireconnect.subscription.service;

import com.hireconnect.subscription.entity.Invoice;
import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.SubscriptionPlan;

import java.util.List;

public interface SubscriptionService {

    Subscription subscribe(Integer recruiterId, SubscriptionPlan plan);

    Subscription cancelSubscription(Integer subscriptionId);

    Subscription renewSubscription(Integer subscriptionId);

    List<Subscription> getByRecruiter(Integer recruiterId);

    Invoice generateInvoice(Subscription subscription);

    List<Invoice> getInvoices(Integer recruiterId);
}