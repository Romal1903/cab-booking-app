package com.example.cabbookingapplication.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingResponseDto {

    private String ratingId;
    private String rideId;
    private String riderId;
    private String driverId;
    private int rating;
    private String review;
    private LocalDateTime createdAt;
}
