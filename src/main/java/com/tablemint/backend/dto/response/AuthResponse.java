package com.tablemint.backend.dto.response;

import com.tablemint.backend.enums.UserRole;

public record AuthResponse(
        String token,
        String userId,
        String name,
        UserRole role
) {}