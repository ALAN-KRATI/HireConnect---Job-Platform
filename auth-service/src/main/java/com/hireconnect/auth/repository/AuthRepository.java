package com.hireconnect.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hireconnect.auth.entity.UserCredential;


public interface AuthRepository extends JpaRepository<UserCredential, UUID> {
    Optional<UserCredential> findByEmail(String email);

    Optional<UserCredential> findById(UUID id);

    boolean existsByEmail(String email);

    void deleteById(UUID id);
}
