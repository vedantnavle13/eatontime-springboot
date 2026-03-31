package com.tablemint.backend.controller;

import com.tablemint.backend.dto.request.CreateRestaurantRequest;
import com.tablemint.backend.dto.response.RestaurantSummaryResponse;
import com.tablemint.backend.dto.response.SlotAvailabilityResponse;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.enums.CuisineType;
import com.tablemint.backend.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantSummaryResponse>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) CuisineType cuisine,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(restaurantService.search(city, locality, cuisine, page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<SlotAvailabilityResponse>> getSlots(
            @PathVariable String id,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "2") int partySize) {
        return ResponseEntity.ok(restaurantService.getSlots(id, date, partySize));
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateRestaurantRequest req) {
        return ResponseEntity.status(201).body(restaurantService.create(userId, req));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> update(
            @PathVariable String id,
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateRestaurantRequest req) {
        return ResponseEntity.ok(restaurantService.update(id, userId, req));
    }
}
