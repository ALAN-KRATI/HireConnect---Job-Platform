package com.hireconnect.auth.service;

import com.hireconnect.auth.config.JwtService;
import com.hireconnect.auth.dto.AuthResponse;
import com.hireconnect.auth.dto.LoginRequest;
import com.hireconnect.auth.dto.SignupRequest;
import com.hireconnect.auth.entity.Provider;
import com.hireconnect.auth.entity.UserCredential;
import com.hireconnect.auth.exception.EmailAlreadyExistsException;
import com.hireconnect.auth.exception.InvalidCredentialsException;
import com.hireconnect.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(SignupRequest request) {

        if (authRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        UserCredential user = UserCredential.builder()
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .provider(Provider.LOCAL)
                .createdAt(LocalDateTime.now())
                .build();

        authRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .refreshToken(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        UserCredential user = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .refreshToken(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void logout(String token) {
        // TODO: implement token blacklist using Redis
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        if (!jwtService.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);

        UserCredential user = authRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("User not found"));

        String newToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}