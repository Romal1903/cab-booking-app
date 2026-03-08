package com.example.cabbookingapplication.dto;

import java.util.UUID;

public record RideResponseDTO(
        UUID id,
        Double pickupLat,
        Double pickupLng,
        Double dropLat,
        Double dropLng,
        Double distanceKm,
        Double fare,
        String status,
        String paymentStatus,
        String riderName,
        String driverName
) {}
