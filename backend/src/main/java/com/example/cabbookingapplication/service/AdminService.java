package com.example.cabbookingapplication.service;

import com.example.cabbookingapplication.dto.AdminRideResponseDto;
import com.example.cabbookingapplication.dto.AdminUserResponseDto;

import java.util.List;
import java.util.Map;

public interface AdminService {

    List<AdminUserResponseDto> getAllUsers();

    List<AdminUserResponseDto> getDrivers();

    void toggleUserStatus(String userId, boolean enabled);

    List<AdminRideResponseDto> getAllRides();

    Map<String, Long> getSystemStats();
}
