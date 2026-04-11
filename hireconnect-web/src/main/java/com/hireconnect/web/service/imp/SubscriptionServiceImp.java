package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.InvoiceDto;
import com.hireconnect.web.dto.SubscriptionDto;
import com.hireconnect.web.exception.BadRequestException;
import com.hireconnect.web.exception.ResourceNotFoundException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.SubscriptionService;
import com.hireconnect.web.util.UrlConstants;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class SubscriptionServiceImp implements SubscriptionService {

    private final RestTemplate restTemplate;

    public SubscriptionServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SubscriptionDto getCurrentPlan(Long recruiterId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.SUBSCRIPTION_SERVICE + "/{recruiterId}",
                    SubscriptionDto.class,
                    recruiterId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Subscription plan not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load current subscription.");
        }
    }

    @Override
    public List<InvoiceDto> getInvoices(Long recruiterId) {
        try {
            ResponseEntity<List<InvoiceDto>> response = restTemplate.exchange(
                    UrlConstants.SUBSCRIPTION_SERVICE + "/{recruiterId}/invoices",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    recruiterId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load invoices.");
        }
    }

    @Override
    public List<SubscriptionDto> getAllSubscriptions() {
        try {
            ResponseEntity<List<SubscriptionDto>> response = restTemplate.exchange(
                    UrlConstants.SUBSCRIPTION_SERVICE,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load subscriptions.");
        }
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {
        try {
            ResponseEntity<List<InvoiceDto>> response = restTemplate.exchange(
                    UrlConstants.SUBSCRIPTION_SERVICE + "/invoices",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load invoices.");
        }
    }

    @Override
    public SubscriptionDto upgradePlan(Long recruiterId, String planName) {
        if (planName == null || planName.isBlank()) {
            throw new BadRequestException("Subscription plan is required.");
        }

        try {
            return restTemplate.postForObject(
                    UrlConstants.SUBSCRIPTION_SERVICE + "/{recruiterId}/upgrade?plan={planName}",
                    null,
                    SubscriptionDto.class,
                    recruiterId,
                    planName
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Recruiter not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to upgrade subscription plan.");
        }
    }

    @Override
    public void cancelPlan(Long recruiterId) {
        try {
            restTemplate.postForObject(
                    UrlConstants.SUBSCRIPTION_SERVICE + "/{recruiterId}/cancel",
                    null,
                    Void.class,
                    recruiterId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Subscription not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to cancel subscription.");
        }
    }

    @Override
    public List<String> getAvailablePlans() {
        try {
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    UrlConstants.SUBSCRIPTION_SERVICE + "/plans",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load available plans.");
        }
    }
}