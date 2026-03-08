package com.example.cabbookingapplication.repository;

import com.example.cabbookingapplication.entity.Driver;
import com.example.cabbookingapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByUser(User user);
}
