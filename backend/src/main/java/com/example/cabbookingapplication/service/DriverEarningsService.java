package com.example.cabbookingapplication.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cabbookingapplication.enums.RideStatus;
import com.example.cabbookingapplication.repository.RideRepository;

@Service
public class DriverEarningsService {

    @Autowired
    private RideRepository rideRepository;

    public double getTotalEarnings(UUID driverId) {
        return rideRepository.getTotalEarnings(driverId, RideStatus.COMPLETED);
    }

    public double getEarningsBetween(
            UUID driverId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return rideRepository.getEarningsBetween(
                driverId,
                RideStatus.COMPLETED,
                from,
                to
        );
    }
}
