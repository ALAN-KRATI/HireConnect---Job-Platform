package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.LoginRequest;
import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.exception.BadRequestException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.AuthService;
import com.hireconnect.web.util.UrlConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImp implements AuthService {

    private final RestTemplate restTemplate;

    public AuthServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String login(LoginRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

            return restTemplate.postForObject(
                    UrlConstants.AUTH_SERVICE + "/login",
                    entity,
                    String.class
            );

        } catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("Invalid email or password.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to connect to authentication service.");
        }
    }

    @Override
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match.");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(
                    UrlConstants.AUTH_SERVICE + "/register",
                    entity,
                    Void.class
            );

        } catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("Registration failed. Please check your details.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to connect to authentication service.");
        }
    }

    @Override
    public void logout(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.postForObject(
                    UrlConstants.AUTH_SERVICE + "/logout",
                    entity,
                    Void.class
            );

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to logout right now.");
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            Boolean response = restTemplate.postForObject(
                    UrlConstants.AUTH_SERVICE + "/validate",
                    entity,
                    Boolean.class
            );

            return Boolean.TRUE.equals(response);

        } catch (RestClientException ex) {
            return false;
        }
    }

    @Override
    public String refreshToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(refreshToken, headers);

            return restTemplate.postForObject(
                    UrlConstants.AUTH_SERVICE + "/refresh",
                    entity,
                    String.class
            );

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to refresh token.");
        }
    }
}