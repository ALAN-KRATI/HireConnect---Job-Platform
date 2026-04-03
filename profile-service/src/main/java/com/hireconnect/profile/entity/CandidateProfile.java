package com.hireconnect.profile.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile extends UserProfile {

    @NotEmpty(message = "At least one skill is required")
    @ElementCollection
    private List<@NotBlank(message = "Skill cannot be blank") String> skills;

    @PositiveOrZero(message = "Experience cannot be negative")
    private int experience;

    @NotBlank(message = "Resume URL is required")
    private String resumeUrl;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Valid
    @NotEmpty(message = "At least one address is required")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Address> addresses;
}