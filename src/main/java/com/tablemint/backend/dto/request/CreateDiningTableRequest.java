package com.tablemint.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDiningTableRequest(
        @NotBlank String tableNumber,
        @NotNull @Min(1) Integer capacity,
        boolean isOutdoor,
        boolean isAccessible
) {}
