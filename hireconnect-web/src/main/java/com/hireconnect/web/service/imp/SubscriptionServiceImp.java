package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.InvoiceDto;
import com.hireconnect.web.dto.SubscriptionDto;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class SubscriptionServiceImp implements SubscriptionService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://subscription-service/subscriptions";

    public SubscriptionServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SubscriptionDto getCurrentPlan(Long recruiterId) {

        return restTemplate.getForObject(
                BASE_URL + "/{recruiterId}",
                SubscriptionDto.class,
                recruiterId
        );
    }

    @Override
    public List<InvoiceDto> getInvoices(Long recruiterId) {

        ResponseEntity<List<InvoiceDto>> response = restTemplate.exchange(
                BASE_URL + "/{recruiterId}/invoices",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InvoiceDto>>() {},
                recruiterId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public List<SubscriptionDto> getAllSubscriptions() {

        ResponseEntity<List<SubscriptionDto>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SubscriptionDto>>() {}
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {

        ResponseEntity<List<InvoiceDto>> response = restTemplate.exchange(
                BASE_URL + "/invoices",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InvoiceDto>>() {}
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public SubscriptionDto upgradePlan(Long recruiterId, String planName) {

        return restTemplate.postForObject(
                BASE_URL + "/{recruiterId}/upgrade?plan={planName}",
                null,
                SubscriptionDto.class,
                recruiterId,
                planName
        );
    }

    @Override
    public void cancelPlan(Long recruiterId) {

        restTemplate.postForObject(
                BASE_URL + "/{recruiterId}/cancel",
                null,
                Void.class,
                recruiterId
        );
    }

    @Override
    public List<String> getAvailablePlans() {

        ResponseEntity<List<String>> response = restTemplate.exchange(
                BASE_URL + "/plans",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }
}