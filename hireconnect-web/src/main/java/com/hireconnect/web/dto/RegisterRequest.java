package com.hireconnect.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Enter a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "CANDIDATE|RECRUITER", message = "Role must be CANDIDATE or RECRUITER")
    private String role;
}