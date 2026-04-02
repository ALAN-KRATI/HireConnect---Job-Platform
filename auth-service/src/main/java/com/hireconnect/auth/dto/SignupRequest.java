package com.hireconnect.auth.dto;

import com.hireconnect.auth.entity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;

    @NotNull
    private UserRole role;

    @NotBlank
    private String mobileNumber;
}
