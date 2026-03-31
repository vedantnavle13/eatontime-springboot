package com.tablemint.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReservationRequest(
        @NotBlank String restaurantId,
        @NotNull LocalDate reservationDate,
        @NotNull LocalTime slotTime,
        @Min(1) @Max(20) int partySize,
        String specialRequests
) {}
