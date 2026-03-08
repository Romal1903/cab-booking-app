package com.example.cabbookingapplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DriverLocationDTO {

    private UUID driverId;
    private UUID rideId;
    private double latitude;
    private double longitude;

    public DriverLocationDTO() {}

    public DriverLocationDTO(UUID driverId, UUID rideId, double latitude, double longitude) {
        this.driverId = driverId;
        this.rideId = rideId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
