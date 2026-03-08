package com.example.cabbookingapplication.repository;

import com.example.cabbookingapplication.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, String> {

    Optional<Rating> findByRideId(String rideId);

    Optional<Rating> findByRideIdAndRiderId(String rideId, String riderId);

    List<Rating> findByDriverId(String driverId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.driverId = :driverId")
    Double findAverageRatingByDriverId(@Param("driverId") String driverId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.driverId = :driverId")
    Long countRatingsByDriverId(@Param("driverId") String driverId);
}
