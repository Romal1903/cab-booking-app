package com.example.cabbookingapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SupportTicketAdminDto {

    private UUID id;
    private String subject;
    private String message;
    private String status;

    private String role;
    private String userName;
    private String driverName;

    private UUID rideId;
    private LocalDateTime createdAt;
}
