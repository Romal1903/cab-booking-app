package com.example.cabbookingapplication.repository;

import com.example.cabbookingapplication.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByRide_Id(UUID rideId);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}
