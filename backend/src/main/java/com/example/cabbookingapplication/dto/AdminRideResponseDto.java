package com.example.cabbookingapplication.dto;

public record AdminRideResponseDto(
        String id,
        String riderName,
        String driverName,
        String status,
        Double fare,
        String paymentStatus
) {}
