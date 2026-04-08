package com.hireconnect.web.dto;

import com.hireconnect.web.enums.UserRole;
import com.hireconnect.web.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {

    private Long id;
    private Long userId;

    private String fullName;
    private String email;
    private String phone;
    private String location;

    private String headline;
    private String summary;
    private String skills;
    private String experience;
    private String education;

    private String resumeUrl;
    private Double walletBalance;

    private UserRole role;
    private UserStatus status;
}