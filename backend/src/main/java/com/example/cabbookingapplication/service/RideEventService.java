package com.example.cabbookingapplication.service;

import com.example.cabbookingapplication.dto.RideEventDTO;
import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.enums.RideEventType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RideEventService {

    private final SimpMessagingTemplate messagingTemplate;

    public RideEventService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyDrivers(Ride ride, RideEventType type, String message) {

        RideEventDTO dto = new RideEventDTO(
                ride.getId(),
                type.name(),
                message
        );

        messagingTemplate.convertAndSend("/topic/drivers", dto);

        messagingTemplate.convertAndSend("/topic/admin/rides", dto);
    }

    public void notifyRider(Ride ride, RideEventType type, String message) {

        UUID riderId = ride.getRiderId();
        if (riderId == null) return;

        RideEventDTO dto = new RideEventDTO(
                ride.getId(),
                type.name(),
                message
        );

        messagingTemplate.convertAndSendToUser(
                riderId.toString(),
                "/queue/rider",
                dto
        );

        messagingTemplate.convertAndSend("/topic/admin/rides", dto);
    }

    public void notifyDriver(Ride ride, RideEventType type, String message) {

        UUID driverId = ride.getDriverId();
        if (driverId == null) return;

        RideEventDTO dto = new RideEventDTO(
                ride.getId(),
                type.name(),
                message
        );

        messagingTemplate.convertAndSendToUser(
                driverId.toString(),
                "/queue/driver",
                dto
        );

        messagingTemplate.convertAndSend("/topic/admin/rides", dto);
    }

    public void sendSOSAlert(UUID userId, String role, String message) {

        RideEventDTO dto = new RideEventDTO(
                null,
                RideEventType.SOS_ALERT.name(),
                message
        );

        messagingTemplate.convertAndSend("/topic/admin/sos", dto);

        if ("DRIVER".equals(role)) {
            messagingTemplate.convertAndSend("/topic/drivers", dto);
        }

        if ("RIDER".equals(role)) {
            messagingTemplate.convertAndSend("/topic/riders", dto);
        }
    }
}
