package com.example.cabbookingapplication.dto;

import java.util.UUID;

public record RideEventDTO(
        UUID rideId,
        String event,
        String message
) {}
