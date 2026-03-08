package com.example.cabbookingapplication.controller;

import com.example.cabbookingapplication.dto.DriverLocationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class DriverLocationController {

    private final SimpMessagingTemplate messagingTemplate;

    public DriverLocationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/update")
    public void updateLocation(@RequestBody DriverLocationDTO location) {

        System.out.println("Driver location received: "
            + location.getLatitude() + ", " + location.getLongitude());

        String topic = "/topic/driver-location/" + location.getRideId().toString();

        System.out.println("Publishing to topic: " + topic);

        messagingTemplate.convertAndSend(topic, location);
    }
}
