package com.example.cabbookingapplication.service.impl;

import com.example.cabbookingapplication.dto.RatingRequestDto;
import com.example.cabbookingapplication.dto.RatingResponseDto;
import com.example.cabbookingapplication.entity.Rating;
import com.example.cabbookingapplication.repository.RatingRepository;
import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.enums.RideStatus;
import com.example.cabbookingapplication.repository.RideRepository;
import com.example.cabbookingapplication.service.RatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RideRepository rideRepository;

    public RatingServiceImpl(RatingRepository ratingRepository,
                             RideRepository rideRepository) {
        this.ratingRepository = ratingRepository;
        this.rideRepository = rideRepository;
    }

    @Override
    public RatingResponseDto submitRating(RatingRequestDto dto, String riderId) {

        UUID rideUuid = UUID.fromString(dto.getRideId());

        Ride ride = rideRepository.findById(rideUuid)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getRiderId().toString().equals(riderId)) {
            throw new RuntimeException("You cannot rate someone else's ride");
        }

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new RuntimeException("Rating allowed only after ride completion");
        }

        ratingRepository.findByRideId(dto.getRideId())
                .ifPresent(r -> {
                    throw new RuntimeException("Rating already submitted");
                });

        Rating rating = new Rating();
        rating.setRatingId(UUID.randomUUID().toString());
        rating.setRideId(ride.getId().toString());
        rating.setRiderId(riderId);
        rating.setDriverId(ride.getDriverId().toString());
        rating.setRating(dto.getRating());
        rating.setReview(dto.getReview());
        rating.setCreatedAt(java.time.LocalDateTime.now());

        Rating saved = ratingRepository.save(rating);
        return mapToDto(saved);
    }

    @Override
    public List<RatingResponseDto> getDriverRatings(String driverId) {
        return ratingRepository.findByDriverId(driverId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public double getDriverAverageRating(String driverId) {
        List<Rating> ratings = ratingRepository.findByDriverId(driverId);
        if (ratings.isEmpty()) return 0.0;

        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    private RatingResponseDto mapToDto(Rating rating) {
        RatingResponseDto dto = new RatingResponseDto();
        dto.setRatingId(rating.getRatingId());
        dto.setRideId(rating.getRideId());
        dto.setRiderId(rating.getRiderId());
        dto.setDriverId(rating.getDriverId());
        dto.setRating(rating.getRating());
        dto.setReview(rating.getReview());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }

    @Override
    public boolean hasRideBeenRated(String rideId) {
        return ratingRepository.findByRideId(rideId).isPresent();
    }
}
