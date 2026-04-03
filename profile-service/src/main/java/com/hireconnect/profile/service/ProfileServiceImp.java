package com.hireconnect.profile.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hireconnect.profile.entity.CandidateProfile;
import com.hireconnect.profile.entity.RecruiterProfile;
import com.hireconnect.profile.exception.ProfileNotFoundException;
import com.hireconnect.profile.repository.CandidateProfileRepository;
import com.hireconnect.profile.repository.RecruiterProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService{
    private final CandidateProfileRepository candidateRepository;
    private final RecruiterProfileRepository recruiterRepository;

    @Override
    public CandidateProfile addCandidateProfile(CandidateProfile profile){
        return candidateRepository.save(profile);
    }

    @Override
    public RecruiterProfile addRecruiterProfile(RecruiterProfile profile){
        return recruiterRepository.save(profile);
    }

    @Override
    public CandidateProfile updateCandidateProfile(UUID id , CandidateProfile profile){
        CandidateProfile cp = candidateRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException("Candidate's Profile not found with id: " + id));

        cp.setFullName(profile.getFullName());
        cp.setEmail(profile.getEmail());
        cp.setMobile(profile.getMobile());
        cp.setSkills(profile.getSkills());
        cp.setExperience(profile.getExperience());
        cp.setResumeUrl(profile.getResumeUrl());
        cp.setDob(profile.getDob());
        cp.setGender(profile.getGender());
        cp.setAddresses(profile.getAddresses());

        return candidateRepository.save(cp);
    }

    @Override
    public RecruiterProfile updateRecruiterProfile(UUID profileId, RecruiterProfile profile){
        RecruiterProfile rp = recruiterRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("Recruiter's Profile not found with id: " + profileId));

        rp.setFullName(profile.getFullName());
        rp.setEmail(profile.getEmail());
        rp.setMobile(profile.getMobile());
        rp.setCompanyName(profile.getCompanyName());;
        rp.setCompanySize(profile.getCompanySize());;
        rp.setIndustry(profile.getIndustry());
        rp.setWebsite(profile.getWebsite());
        rp.setOfficeAddress(profile.getOfficeAddress());

        return recruiterRepository.save(rp);
    }

    @Override
    public void deleteCandidateProfile(UUID id){
        CandidateProfile profile = candidateRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException("Candidate Profile not found with id: " + id));
        candidateRepository.delete(profile);
    }

    @Override
    public void deleteRecruiterProfile(UUID id){
        RecruiterProfile profile = recruiterRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException("Recruiter Profile not found with id: " + id));
        recruiterRepository.delete(profile);
    }

    @Override
    public CandidateProfile getCandidateProfile(UUID id){
        return candidateRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException("Candidate Profile not found with id: " + id));
    
    }

    @Override
    public RecruiterProfile getRecruiterProfile(UUID id){
        return recruiterRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException("Recruiter Profile not found with id: " + id));
    }

    @Override
    public CandidateProfile getCandidateProfileByEmail(String email){
        return candidateRepository.findByEmail(email).orElseThrow(() -> new ProfileNotFoundException("Candidate Profile not found with email: " + email));
    
    }

    @Override
    public RecruiterProfile getRecruiterProfileByEmail(String email){
        return recruiterRepository.findByEmail(email).orElseThrow(() -> new ProfileNotFoundException("Recruiter Profile not found with email: " + email));
    }

    @Override
    public List<CandidateProfile> getAllCandidateProfiles(){
        return candidateRepository.findAll();
    
    }

    @Override
    public List<RecruiterProfile> getAllRecruiterProfiles(){
        return recruiterRepository.findAll();
    }
}
