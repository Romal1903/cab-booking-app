package com.example.cabbookingapplication.dto;

public record AdminUserResponseDto(
        String id,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean enabled
) {}
