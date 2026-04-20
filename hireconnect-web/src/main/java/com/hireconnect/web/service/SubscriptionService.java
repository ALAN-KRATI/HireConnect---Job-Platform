package com.hireconnect.web.service;

import com.hireconnect.web.dto.InvoiceDto;
import com.hireconnect.web.dto.SubscriptionDto;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    SubscriptionDto getCurrentPlan(UUID recruiterId);

    List<InvoiceDto> getInvoices(UUID recruiterId);

    List<SubscriptionDto> getAllSubscriptions();

    List<InvoiceDto> getAllInvoices();

    SubscriptionDto upgradePlan(UUID recruiterId, String planName);

    void cancelPlan(UUID recruiterId);

    List<String> getAvailablePlans();
}