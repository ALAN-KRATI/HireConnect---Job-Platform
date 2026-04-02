package com.hireconnect.auth.repository;

import com.hireconnect.auth.entity.Provider;
import com.hireconnect.auth.entity.UserCredential;
import com.hireconnect.auth.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @Test
    void shouldFindUserByEmail() {

        UserCredential user = UserCredential.builder()
                .email("candidate@test.com")
                .mobileNumber("9876543210")
                .password("encoded-password")
                .role(UserRole.CANDIDATE)
                .provider(Provider.LOCAL)
                .createdAt(LocalDateTime.now())
                .build();

        authRepository.save(user);

        var result = authRepository.findByEmail("candidate@test.com");

        assertTrue(result.isPresent());
        assertEquals("candidate@test.com", result.get().getEmail());
    }
}