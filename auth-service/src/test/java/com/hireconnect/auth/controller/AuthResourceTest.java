package com.hireconnect.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireconnect.auth.dto.AuthResponse;
import com.hireconnect.auth.dto.LoginRequest;
import com.hireconnect.auth.dto.SignupRequest;
import com.hireconnect.auth.entity.UserRole;
import com.hireconnect.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthResource.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void shouldRegisterSuccessfully() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setEmail("candidate@test.com");
        request.setMobileNumber("9876543210");
        request.setPassword("Password@123");
        request.setRole(UserRole.CANDIDATE);

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("jwt-token")
                .email("candidate@test.com")
                .role("CANDIDATE")
                .build();

        when(authService.register(any(SignupRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("candidate@test.com");
        request.setPassword("Password@123");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("jwt-token")
                .email("candidate@test.com")
                .role("CANDIDATE")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}