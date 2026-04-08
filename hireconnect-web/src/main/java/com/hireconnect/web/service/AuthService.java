package com.hireconnect.web.service;

import com.hireconnect.web.dto.LoginRequest;
import com.hireconnect.web.dto.RegisterRequest;

public interface AuthService {

    String login(LoginRequest request);

    void register(RegisterRequest request);

    void logout(String token);

    boolean validateToken(String token);

    String refreshToken(String refreshToken);
}