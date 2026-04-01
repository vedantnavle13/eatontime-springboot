package com.tablemint.backend.controller;

import com.tablemint.backend.dto.request.SetOperatingHoursRequest;
import com.tablemint.backend.entity.OperatingHours;
import com.tablemint.backend.service.OperatingHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants/{restaurantId}/hours")
@RequiredArgsConstructor
public class OperatingHoursController {

    private final OperatingHoursService hoursService;

    // GET operating hours for a restaurant (public)
    @GetMapping
    public ResponseEntity<List<OperatingHours>> getHours(@PathVariable String restaurantId) {
        return ResponseEntity.ok(hoursService.getByRestaurant(restaurantId));
    }

    // POST set hours for a single day (owner only) — upserts
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<OperatingHours> setHours(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal String ownerId,
            @Valid @RequestBody SetOperatingHoursRequest req) {
        return ResponseEntity.ok(hoursService.setHours(restaurantId, ownerId, req));
    }

    // POST /bulk — set all 7 days at once (owner only)
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<OperatingHours>> setBulkHours(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal String ownerId,
            @RequestBody List<@Valid SetOperatingHoursRequest> requests) {
        return ResponseEntity.ok(hoursService.setBulkHours(restaurantId, ownerId, requests));
    }
}
