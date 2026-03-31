package com.tablemint.backend.dto.request;

import com.tablemint.backend.enums.CuisineType;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateRestaurantRequest(
        @NotBlank String name,
        String description,
        @NotBlank String address,
        @NotBlank String city,
        String locality,
        String phone,
        List<CuisineType> cuisines,
        Integer averageCostForTwo
) {}