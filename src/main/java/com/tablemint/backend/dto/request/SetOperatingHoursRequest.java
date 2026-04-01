package com.tablemint.backend.dto.request;

import com.tablemint.backend.enums.OperatingDay;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SetOperatingHoursRequest(
        @NotNull OperatingDay day,
        LocalTime openTime,
        LocalTime closeTime,
        boolean closed
) {}
