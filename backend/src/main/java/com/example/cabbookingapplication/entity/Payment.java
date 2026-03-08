package com.example.cabbookingapplication.entity;

import com.example.cabbookingapplication.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = @UniqueConstraint(columnNames = "ride_id")
)
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    @JsonBackReference
    private Ride ride;

    @Column(nullable = false)
    private UUID riderId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String stripePaymentIntentId;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
