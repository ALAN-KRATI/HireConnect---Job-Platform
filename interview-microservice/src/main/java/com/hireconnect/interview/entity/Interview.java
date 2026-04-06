package com.hireconnect.interview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.hireconnect.interview.enums.InterviewMode;
import com.hireconnect.interview.enums.InterviewStatus;

@Entity
@Table(name = "interviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;

    private Long applicationId;

    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private InterviewMode mode;

    private String meetLink;

    private String location;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    @Column(length = 1000)
    private String notes;

     @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;
}