package com.example.cabbookingapplication.service;

import com.example.cabbookingapplication.dto.RatingRequestDto;
import com.example.cabbookingapplication.dto.RatingResponseDto;

import java.util.List;

public interface RatingService {

    RatingResponseDto submitRating(RatingRequestDto dto, String riderId);

    List<RatingResponseDto> getDriverRatings(String driverId);

    double getDriverAverageRating(String driverId);

    boolean hasRideBeenRated(String rideId);
}
