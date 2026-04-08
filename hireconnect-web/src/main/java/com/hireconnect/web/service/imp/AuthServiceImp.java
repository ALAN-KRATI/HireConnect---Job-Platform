package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.LoginRequest;
import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImp implements AuthService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8081/auth";

    @Override
    public String login(LoginRequest request) {
        return restTemplate.postForObject(BASE_URL + "/login", request, String.class);
    }

    @Override
    public void register(RegisterRequest request) {
        restTemplate.postForObject(BASE_URL + "/register", request, Void.class);
    }

    @Override
    public void logout(String token) {
        restTemplate.postForObject(BASE_URL + "/logout", token, Void.class);
    }

    @Override
    public boolean validateToken(String token) {
        return restTemplate.postForObject(BASE_URL + "/validate", token, Boolean.class);
    }

    @Override
    public String refreshToken(String refreshToken) {
        return restTemplate.postForObject(
                BASE_URL + "/refresh",
                refreshToken,
                String.class);
    }
}