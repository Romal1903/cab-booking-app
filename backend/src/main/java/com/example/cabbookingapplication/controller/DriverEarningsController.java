package com.example.cabbookingapplication.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.cabbookingapplication.entity.Driver;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.service.DriverEarningsService;

@RestController
@RequestMapping("/api/driver/earnings")
public class DriverEarningsController {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverEarningsService earningsService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('DRIVER')")
    public Map<String, Object> earningsSummary() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Driver driver = driverRepository.findByUser(user).orElseThrow();

        LocalDateTime now = LocalDateTime.now();

        return Map.of(
            "total", earningsService.getTotalEarnings(driver.getId()),
            "today", earningsService.getEarningsBetween(
                    driver.getId(),
                    now.toLocalDate().atStartOfDay(),
                    now
            ),
            "weekly", earningsService.getEarningsBetween(
                    driver.getId(),
                    now.minusDays(7),
                    now
            )
        );
    }
}
