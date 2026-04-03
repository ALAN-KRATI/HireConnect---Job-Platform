package com.hireconnect.profile.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class RecruiterProfile extends UserProfile {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Company size is required")
    private String companySize;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotBlank(message = "Website is required")
    @Pattern(
        regexp = "^(https?:\\/\\/)?([\\w\\-]+)+\\.([a-zA-Z]{2,})([\\/\\w\\-.]*)*\\/?$",
        message = "Invalid website URL"
    )
    private String website;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private Address officeAddress;
}