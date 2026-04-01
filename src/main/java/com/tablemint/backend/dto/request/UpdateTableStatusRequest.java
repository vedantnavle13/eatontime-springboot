package com.tablemint.backend.dto.request;

import com.tablemint.backend.enums.TableStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTableStatusRequest(
        @NotNull TableStatus status
) {}
