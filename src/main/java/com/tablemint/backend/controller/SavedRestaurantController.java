package com.tablemint.backend.controller;

import com.tablemint.backend.dto.response.RestaurantSummaryResponse;
import com.tablemint.backend.service.SavedRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved")
@RequiredArgsConstructor
public class SavedRestaurantController {

    private final SavedRestaurantService savedService;

    @PostMapping("/{restaurantId}")
    public ResponseEntity<Void> save(
            @AuthenticationPrincipal String userId,
            @PathVariable String restaurantId) {
        savedService.save(userId, restaurantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> unsave(
            @AuthenticationPrincipal String userId,
            @PathVariable String restaurantId) {
        savedService.unsave(userId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RestaurantSummaryResponse>> getSaved(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(savedService.getSaved(userId));
    }
}
