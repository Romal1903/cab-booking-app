package com.example.cabbookingapplication.repository;

import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByRiderId(UUID riderId);

    List<Ride> findByRiderIdOrderByBookedAtDesc(UUID riderId);

    List<Ride> findByStatusOrderByBookedAtDesc(RideStatus status);

    List<Ride> findByDriverIdAndStatusInOrderByBookedAtDesc(UUID driverId, List<RideStatus> statuses);

    List<Ride> findByDriverIdAndStatusOrderByBookedAtDesc(UUID driverId, RideStatus status);

    List<Ride> findByDriverIdAndStatusIn(UUID driverId, List<RideStatus> statuses);

    List<Ride> findByDriverIdAndStatus(UUID driverId, RideStatus status);

    List<Ride> findAllByOrderByBookedAtDesc();

    @Query("""
        SELECT COALESCE(SUM(r.fare), 0)
        FROM Ride r
        WHERE r.driverId = :driverId
        AND r.status = :status
    """)
    double getTotalEarnings(
            @Param("driverId") UUID driverId,
            @Param("status") RideStatus status
    );

    @Query("""
        SELECT COALESCE(SUM(r.fare), 0)
        FROM Ride r
        WHERE r.driverId = :driverId
        AND r.status = :status
        AND r.bookedAt BETWEEN :from AND :to
    """)
    double getEarningsBetween(
            @Param("driverId") UUID driverId,
            @Param("status") RideStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
