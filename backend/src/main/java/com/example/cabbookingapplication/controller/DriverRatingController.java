package com.example.cabbookingapplication.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.cabbookingapplication.entity.Driver;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.service.DriverRatingService;

@RestController
@RequestMapping("/api/driver/ratings")
public class DriverRatingController {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverRatingService ratingService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('DRIVER')")
    public Map<String, Object> ratingSummary() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Driver driver = driverRepository.findByUser(user).orElseThrow();

        String driverId = driver.getId().toString();

        return Map.of(
            "average", ratingService.getAverageRating(driverId),
            "count", ratingService.getTotalRatings(driverId)
        );
    }
}
