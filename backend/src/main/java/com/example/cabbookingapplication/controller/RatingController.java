package com.example.cabbookingapplication.controller;

import com.example.cabbookingapplication.dto.RatingRequestDto;
import com.example.cabbookingapplication.dto.RatingResponseDto;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.repository.RatingRepository;
import com.example.cabbookingapplication.service.RatingService;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final RatingRepository ratingRepository;

    public RatingController(RatingService ratingService, RatingRepository ratingRepository) {
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('RIDER')")
    public RatingResponseDto submitRating(
            @Valid @RequestBody RatingRequestDto dto,
            Authentication authentication
    ) {
        User rider = (User) authentication.getPrincipal();
        return ratingService.submitRating(dto, rider.getId().toString());
    }

    @GetMapping("/driver/{driverId}")
    public List<RatingResponseDto> getDriverRatings(@PathVariable String driverId) {
        return ratingService.getDriverRatings(driverId);
    }

    @GetMapping("/driver/{driverId}/average")
    public double getAverageRating(@PathVariable String driverId) {
        return ratingService.getDriverAverageRating(driverId);
    }

    @GetMapping("/rider/exists/{rideId}")
    public boolean hasRated(@PathVariable String rideId) {
        return ratingService.hasRideBeenRated(rideId);
    }

    @GetMapping("/ride/{rideId}")
    @PreAuthorize("hasRole('RIDER')")
    public Map<String, Object> getRatingForRide(@PathVariable String rideId) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ratingRepository
                .findByRideIdAndRiderId(rideId, user.getId().toString())
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("rating", r.getRating());
                    map.put("review", r.getReview());
                    return map;
                })
                .orElse(Collections.emptyMap());
    }
}
