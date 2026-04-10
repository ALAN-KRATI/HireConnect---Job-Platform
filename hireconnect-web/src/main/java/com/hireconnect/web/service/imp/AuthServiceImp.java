package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.LoginRequest;
import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.AuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImp implements AuthService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://auth-service/auth";

    public AuthServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String login(LoginRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(
                BASE_URL + "/login",
                entity,
                String.class
        );
    }

    @Override
    public void register(RegisterRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForObject(
                BASE_URL + "/register",
                entity,
                Void.class
        );
    }

    @Override
    public void logout(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.postForObject(
                BASE_URL + "/logout",
                entity,
                Void.class
        );
    }

    @Override
    public boolean validateToken(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Boolean response = restTemplate.postForObject(
                BASE_URL + "/validate",
                entity,
                Boolean.class
        );

        return response != null && response;
    }

    @Override
    public String refreshToken(String refreshToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(refreshToken, headers);

        return restTemplate.postForObject(
                BASE_URL + "/refresh",
                entity,
                String.class
        );
    }
}