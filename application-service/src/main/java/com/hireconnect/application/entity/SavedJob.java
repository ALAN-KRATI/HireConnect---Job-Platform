package com.hireconnect.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "saved_jobs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedJob {

    @Id
    private UUID id;

    private UUID candidateId;

    private UUID jobId;
}