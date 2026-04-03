package com.hireconnect.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hireconnect.profile.entity.CandidateProfile;
import com.hireconnect.profile.entity.RecruiterProfile;
import com.hireconnect.profile.exception.ProfileNotFoundException;
import com.hireconnect.profile.repository.CandidateProfileRepository;
import com.hireconnect.profile.repository.RecruiterProfileRepository;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {
    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @InjectMocks
    private ProfileServiceImp profileService;

    private CandidateProfile candidateProfile;
    private RecruiterProfile recruiterProfile;

    @BeforeEach
    void setUp() {
        candidateProfile = CandidateProfile.builder()
                .experience(2)
                .resumeUrl("resume.pdf")
                .gender("Female")
                .build();

        candidateProfile.setProfileId(UUID.randomUUID());
        candidateProfile.setFullName("Alankrati Sharma");
        candidateProfile.setEmail("alan@test.com");
        candidateProfile.setMobile("9876543210");

        recruiterProfile = RecruiterProfile.builder()
                .companyName("HireConnect")
                .companySize("100")
                .industry("IT")
                .website("https://hireconnect.com")
                .build();

        recruiterProfile.setProfileId(UUID.randomUUID());
        recruiterProfile.setFullName("Recruiter One");
        recruiterProfile.setEmail("recruiter@test.com");
        recruiterProfile.setMobile("9876543211");
    }

    @Test
    void shouldAddCandidateProfile() {
        when(candidateProfileRepository.save(any(CandidateProfile.class)))
                .thenReturn(candidateProfile);

        CandidateProfile saved = profileService.addCandidateProfile(candidateProfile);

        assertEquals("Alankrati Sharma", saved.getFullName());
        verify(candidateProfileRepository).save(candidateProfile);
    }

    @Test
    void shouldGetCandidateProfile() {
        when(candidateProfileRepository.findById(candidateProfile.getProfileId()))
                .thenReturn(Optional.of(candidateProfile));

        CandidateProfile found = profileService.getCandidateProfile(candidateProfile.getProfileId());

        assertEquals(candidateProfile.getEmail(), found.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenCandidateNotFound() {
        UUID id = UUID.randomUUID();

        when(candidateProfileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class,
                () -> profileService.getCandidateProfile(id));
    }

    @Test
    void shouldGetAllRecruiters() {
        when(recruiterProfileRepository.findAll())
                .thenReturn(List.of(recruiterProfile));

        List<RecruiterProfile> recruiters = profileService.getAllRecruiterProfiles();

        assertEquals(1, recruiters.size());
        assertEquals("HireConnect", recruiters.get(0).getCompanyName());
    }
}

