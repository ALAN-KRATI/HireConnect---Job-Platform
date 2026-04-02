package com.hireconnect.auth.service;

import com.hireconnect.auth.config.JwtService;
import com.hireconnect.auth.dto.LoginRequest;
import com.hireconnect.auth.dto.SignupRequest;
import com.hireconnect.auth.entity.Provider;
import com.hireconnect.auth.entity.UserCredential;
import com.hireconnect.auth.entity.UserRole;
import com.hireconnect.auth.exception.EmailAlreadyExistsException;
import com.hireconnect.auth.exception.InvalidCredentialsException;
import com.hireconnect.auth.repository.AuthRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImp authService;

    private SignupRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new SignupRequest();
        registerRequest.setEmail("candidate@test.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole(UserRole.CANDIDATE);
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        when(authRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");

        var response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("candidate@test.com", response.getEmail());
        assertEquals("jwt-token", response.getToken());

        verify(authRepository, times(1)).save(any(UserCredential.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        when(authRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(registerRequest));

        verify(authRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("candidate@test.com");
        loginRequest.setPassword("Password@123");

        UserCredential user = UserCredential.builder()
                .email("candidate@test.com")
                .password("encoded-password")
                .role(UserRole.CANDIDATE)
                .provider(Provider.LOCAL)
                .createdAt(LocalDateTime.now())
                .build();

        when(authRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");

        var response = authService.login(loginRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals("CANDIDATE", response.getRole());
    }

    @Test
    void shouldThrowExceptionForWrongPassword() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("candidate@test.com");
        loginRequest.setPassword("wrong-password");

        UserCredential user = UserCredential.builder()
                .email("candidate@test.com")
                .password("encoded-password")
                .role(UserRole.CANDIDATE)
                .build();

        when(authRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequest));
    }
}
