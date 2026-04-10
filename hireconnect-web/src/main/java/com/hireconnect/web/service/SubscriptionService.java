package com.hireconnect.web.service;

import com.hireconnect.web.dto.InvoiceDto;
import com.hireconnect.web.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto getCurrentPlan(Long recruiterId);

    List<InvoiceDto> getInvoices(Long recruiterId);

    List<SubscriptionDto> getAllSubscriptions();

    List<InvoiceDto> getAllInvoices();

    SubscriptionDto upgradePlan(Long recruiterId, String planName);

    void cancelPlan(Long recruiterId);

    List<String> getAvailablePlans();
}