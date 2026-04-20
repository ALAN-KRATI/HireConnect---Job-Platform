package com.hireconnect.web.dto;

import com.hireconnect.web.enums.UserRole;
import com.hireconnect.web.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private Long profileId;
    private UUID userId;

    private String fullName;
    private String email;
    private String phone;
    private String location;

    private String profileImageUrl;

    private String headline;
    private String summary;

    private List<String> skills;
    private String experience;
    private String education;

    private String companyName;
    private String companyDescription;
    private String companyWebsite;

    private String resumeUrl;

    private Double walletBalance;

    private UserRole role;
    private UserStatus status;
}