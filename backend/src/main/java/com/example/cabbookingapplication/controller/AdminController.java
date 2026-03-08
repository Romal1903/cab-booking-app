package com.example.cabbookingapplication.controller;

import com.example.cabbookingapplication.dto.AdminRideResponseDto;
import com.example.cabbookingapplication.dto.AdminUserResponseDto;
import com.example.cabbookingapplication.service.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<AdminUserResponseDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/drivers")
    public List<AdminUserResponseDto> getDrivers() {
        return adminService.getDrivers();
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Void> toggleUser(
            @PathVariable String id,
            @RequestParam boolean enabled
    ) {
        adminService.toggleUserStatus(id, enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rides")
    public List<AdminRideResponseDto> getAllRides() {
        return adminService.getAllRides();
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return adminService.getSystemStats();
    }
}
