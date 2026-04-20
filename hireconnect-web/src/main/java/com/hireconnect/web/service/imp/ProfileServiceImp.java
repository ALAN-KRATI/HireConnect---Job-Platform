package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.ProfileDto;
import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.exception.BadRequestException;
import com.hireconnect.web.exception.ResourceNotFoundException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.util.UrlConstants;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileServiceImp implements ProfileService {

    private final RestTemplate restTemplate;

    public ProfileServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void registerCandidate(RegisterRequest request) {
        try {
            restTemplate.postForObject(
                    UrlConstants.PROFILE_SERVICE + "/candidate",
                    request,
                    Void.class
            );
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("Invalid candidate registration details.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to register candidate profile.");
        }
    }

    @Override
    public void registerRecruiter(RegisterRequest request) {
        try {
            restTemplate.postForObject(
                    UrlConstants.PROFILE_SERVICE + "/recruiter",
                    request,
                    Void.class
            );
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("Invalid recruiter registration details.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to register recruiter profile.");
        }
    }

    @Override
    public ProfileDto getCandidateProfile(UUID candidateId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.PROFILE_SERVICE + "/candidate/{candidateId}",
                    ProfileDto.class,
                    candidateId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Candidate profile not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load candidate profile.");
        }
    }

    @Override
    public ProfileDto getRecruiterProfile(UUID recruiterId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.PROFILE_SERVICE + "/recruiter/{recruiterId}",
                    ProfileDto.class,
                    recruiterId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Recruiter profile not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load recruiter profile.");
        }
    }

    @Override
    public ProfileDto updateProfile(UUID userId, ProfileDto dto) {
        try {
            restTemplate.put(
                    UrlConstants.PROFILE_SERVICE + "/{userId}",
                    dto,
                    userId
            );

            return getUserById(userId);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("User profile not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to update profile.");
        }
    }

    @Override
    public List<ProfileDto> getAllUsers() {
        try {
            ResponseEntity<List<ProfileDto>> response = restTemplate.exchange(
                    UrlConstants.PROFILE_SERVICE,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load users.");
        }
    }

    @Override
    public void suspendUser(UUID userId) {
        updateUserStatus(userId, "suspend", "suspend");
    }

    @Override
    public void activateUser(UUID userId) {
        updateUserStatus(userId, "activate", "activate");
    }

    @Override
    public void bookmarkJob(UUID candidateId, Long jobId) {
        try {
            restTemplate.postForObject(
                    UrlConstants.PROFILE_SERVICE + "/{candidateId}/bookmark/{jobId}",
                    null,
                    Void.class,
                    candidateId,
                    jobId
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to bookmark job.");
        }
    }

    @Override
    public void addMoneyToWallet(UUID candidateId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("Wallet amount must be greater than 0.");
        }

        try {
            restTemplate.postForObject(
                    UrlConstants.PROFILE_SERVICE + "/{candidateId}/wallet?amount={amount}",
                    null,
                    Void.class,
                    candidateId,
                    amount
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to add money to wallet.");
        }
    }

    @Override
    public ProfileDto getUserById(UUID userId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.PROFILE_SERVICE + "/{userId}",
                    ProfileDto.class,
                    userId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("User not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load user profile.");
        }
    }

    @Override
    public void updateWalletBalance(UUID userId, Double amount) {
        if (amount == null) {
            throw new BadRequestException("Amount is required.");
        }

        try {
            restTemplate.postForObject(
                    UrlConstants.PROFILE_SERVICE + "/{userId}/wallet/update?amount={amount}",
                    null,
                    Void.class,
                    userId,
                    amount
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to update wallet balance.");
        }
    }

    @Override
    public UUID getCandidateIdByEmail(String email) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.PROFILE_SERVICE + "/candidate/email/{email}",
                    UUID.class,
                    email
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Candidate not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to fetch candidate details.");
        }
    }

    @Override
    public UUID getRecruiterIdByEmail(String email) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.PROFILE_SERVICE + "/recruiter/email/{email}",
                    UUID.class,
                    email
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Recruiter not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to fetch recruiter details.");
        }
    }

    private void updateUserStatus(UUID userId, String action, String actionName) {
        try {
            restTemplate.postForObject(
                    UrlConstants.PROFILE_SERVICE + "/{userId}/" + action,
                    null,
                    Void.class,
                    userId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("User not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "Unable to " + actionName + " user right now."
            );
        }
    }
}