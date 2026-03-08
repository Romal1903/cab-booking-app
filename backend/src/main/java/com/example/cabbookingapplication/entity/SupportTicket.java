package com.example.cabbookingapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class SupportTicket {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private String role;

    private UUID rideId;

    private String subject;

    @Column(length = 2000)
    private String message;

    private String status;

    private LocalDateTime createdAt;
}
