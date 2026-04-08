package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.InvoiceDto;
import com.hireconnect.web.dto.SubscriptionDto;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class SubscriptionServiceImp implements SubscriptionService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8087/subscriptions";

    @Override
    public SubscriptionDto getCurrentPlan(Long recruiterId) {
        return restTemplate.getForObject(BASE_URL + "/" + recruiterId, SubscriptionDto.class);
    }

    @Override
    public List<InvoiceDto> getInvoices(Long recruiterId) {
        InvoiceDto[] response = restTemplate.getForObject(BASE_URL + "/" + recruiterId + "/invoices",
                InvoiceDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public List<SubscriptionDto> getAllSubscriptions() {
        SubscriptionDto[] response = restTemplate.getForObject(BASE_URL, SubscriptionDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {
        InvoiceDto[] response = restTemplate.getForObject(BASE_URL + "/invoices", InvoiceDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public SubscriptionDto upgradePlan(Long recruiterId, String planName) {
        return restTemplate.postForObject(
                BASE_URL + "/" + recruiterId + "/upgrade?plan=" + planName,
                null,
                SubscriptionDto.class);
    }

    @Override
    public void cancelPlan(Long recruiterId) {
        restTemplate.postForObject(
                BASE_URL + "/" + recruiterId + "/cancel",
                null,
                Void.class);
    }
}