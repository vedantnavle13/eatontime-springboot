package com.tablemint.backend.dto.response;

import java.time.LocalTime;

public record SlotAvailabilityResponse(
        LocalTime slotTime,
        int availableTables,
        boolean available
) {}
