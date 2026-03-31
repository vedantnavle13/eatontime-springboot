package com.tablemint.backend.dto.response;

import com.tablemint.backend.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservationResponse(
        String id,
        String restaurantName,
        String restaurantId,
        LocalDate reservationDate,
        LocalTime slotTime,
        int partySize,
        ReservationStatus status,
        String qrCodeToken,
        LocalDateTime createdAt
) {}
