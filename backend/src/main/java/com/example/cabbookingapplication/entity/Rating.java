package com.example.cabbookingapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Getter
@Setter
public class Rating {

    @Id
    @Column(name = "rating_id")
    private String ratingId;

    @Column(name = "ride_id", nullable = false)
    private String rideId;

    @Column(name = "rider_id", nullable = false)
    private String riderId;

    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @Column(nullable = false)
    private int rating;

    @Column
    private String review;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
