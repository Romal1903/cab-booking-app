package com.example.cabbookingapplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cabbookingapplication.repository.RatingRepository;

@Service
public class DriverRatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public double getAverageRating(String driverId) {
        Double avg = ratingRepository.findAverageRatingByDriverId(driverId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public long getTotalRatings(String driverId) {
        return ratingRepository.countRatingsByDriverId(driverId);
    }
}
