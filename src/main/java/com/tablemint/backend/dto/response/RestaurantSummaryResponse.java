package com.tablemint.backend.dto.response;

import com.tablemint.backend.enums.CuisineType;

import java.util.List;

public record RestaurantSummaryResponse(
        String id,
        String name,
        String locality,
        String city,
        String coverImageUrl,
        Double averageRating,
        Integer totalReviews,
        Integer averageCostForTwo,
        List<CuisineType> cuisines
) {}