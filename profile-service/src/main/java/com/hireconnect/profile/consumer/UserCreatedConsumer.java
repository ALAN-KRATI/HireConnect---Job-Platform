package com.hireconnect.profile.consumer;

import com.hireconnect.profile.config.RabbitMQConfig;
import com.hireconnect.profile.entity.CandidateProfile;
import com.hireconnect.profile.entity.RecruiterProfile;
import com.hireconnect.profile.event.UserCreatedEvent;
import com.hireconnect.profile.repository.CandidateProfileRepository;
import com.hireconnect.profile.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final CandidateProfileRepository candidateRepository;
    private final RecruiterProfileRepository recruiterRepository;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void consume(UserCreatedEvent event) {

        log.info("Received UserCreatedEvent: {}", event);

        if ("CANDIDATE".equalsIgnoreCase(event.getRole())) {

            CandidateProfile profile = CandidateProfile.builder()
                    .skills(List.of())
                    .experience(0)
                    .resumeUrl("")
                    .dob(LocalDate.of(2000, 1, 1))
                    .gender("Not Specified")
                    .addresses(List.of())
                    .build();

            profile.setUserId(event.getUserId());
            profile.setEmail(event.getEmail());
            profile.setMobile(event.getMobileNumber());
            profile.setFullName("New Candidate");

            candidateRepository.save(profile);

            log.info("Candidate profile created for {}", event.getEmail());
        } else if ("RECRUITER".equalsIgnoreCase(event.getRole())) {

            RecruiterProfile profile = RecruiterProfile.builder()
                    .build();

            profile.setUserId(event.getUserId());
            profile.setEmail(event.getEmail());
            profile.setMobile(event.getMobileNumber());
            profile.setFullName("New Recruiter");

            recruiterRepository.save(profile);
        }
    }
}