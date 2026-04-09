package com.hireconnect.profile.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile extends UserProfile {

    private String headline;

    private String location;

    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> skills;

    private Integer experience;

    private LocalDate dob;

    private String gender;

    private String resumeUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Address> addresses;
}