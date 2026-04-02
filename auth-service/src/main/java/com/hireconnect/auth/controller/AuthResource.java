package com.hireconnect.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hireconnect.auth.dto.AuthResponse;
import com.hireconnect.auth.dto.LoginRequest;
import com.hireconnect.auth.dto.RefreshTokenRequest;
import com.hireconnect.auth.dto.SignupRequest;
import com.hireconnect.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthResource {
    private final AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> singup(@Valid @RequestBody SignupRequest request) {
       return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
       return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
       service.logout(token);
       return ResponseEntity.ok("Logged out succesfully!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
       return ResponseEntity.ok(service.refreshToken(request.getRefreshToken()));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody String token) {
       return ResponseEntity.ok(service.validateToken(token));
    }
    
}
