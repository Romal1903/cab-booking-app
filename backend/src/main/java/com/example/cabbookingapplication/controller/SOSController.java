package com.example.cabbookingapplication.controller;

import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.enums.RideEventType;
import com.example.cabbookingapplication.repository.RideRepository;
import com.example.cabbookingapplication.service.RideEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sos")
public class SOSController {

    private final RideRepository rideRepository;
    private final RideEventService rideEventService;

    public SOSController(RideRepository rideRepository,
                         RideEventService rideEventService) {
        this.rideRepository = rideRepository;
        this.rideEventService = rideEventService;
    }

    @PostMapping("/{rideId}")
    public ResponseEntity<?> triggerSOS(@PathVariable UUID rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        rideEventService.notifyDrivers(
                ride,
                RideEventType.SOS_ALERT,
                "SOS ALERT triggered for ride " + rideId
        );

        rideEventService.notifyRider(
                ride,
                RideEventType.SOS_ALERT,
                "SOS ALERT sent. Help is being notified."
        );

        return ResponseEntity.ok().build();
    }
}
