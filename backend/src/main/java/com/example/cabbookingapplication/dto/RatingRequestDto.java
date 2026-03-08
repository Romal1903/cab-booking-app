package com.example.cabbookingapplication.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequestDto {

    @NotBlank
    private String rideId;

    @Min(1)
    @Max(5)
    private int rating;

    private String review;
}
