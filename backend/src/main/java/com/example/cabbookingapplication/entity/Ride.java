package com.example.cabbookingapplication.entity;

import com.example.cabbookingapplication.enums.RideStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Getter
@Setter
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID riderId;
    private UUID driverId;

    private Double pickupLat;
    private Double pickupLng;

    private Double dropLat;
    private Double dropLng;

    private Double distanceKm;
    private Double fare;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    @OneToOne(mappedBy = "ride", fetch = FetchType.LAZY)
    @JsonIgnore
    private Payment payment;
}
