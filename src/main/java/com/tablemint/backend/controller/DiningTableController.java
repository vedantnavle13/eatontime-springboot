package com.tablemint.backend.controller;

import com.tablemint.backend.dto.request.CreateDiningTableRequest;
import com.tablemint.backend.dto.request.UpdateTableStatusRequest;
import com.tablemint.backend.entity.DiningTable;
import com.tablemint.backend.service.DiningTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiningTableController {

    private final DiningTableService tableService;

    // GET all tables for a restaurant (public — customers see availability)
    @GetMapping("/restaurants/{restaurantId}/tables")
    public ResponseEntity<List<DiningTable>> getTables(@PathVariable String restaurantId) {
        return ResponseEntity.ok(tableService.getByRestaurant(restaurantId));
    }

    // POST add a new table (owner only)
    @PostMapping("/restaurants/{restaurantId}/tables")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<DiningTable> addTable(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal String ownerId,
            @Valid @RequestBody CreateDiningTableRequest req) {
        return ResponseEntity.status(201)
                .body(tableService.addTable(restaurantId, ownerId, req));
    }

    // PATCH update table status (owner only)
    @PatchMapping("/tables/{tableId}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<DiningTable> updateStatus(
            @PathVariable String tableId,
            @AuthenticationPrincipal String ownerId,
            @Valid @RequestBody UpdateTableStatusRequest req) {
        return ResponseEntity.ok(tableService.updateStatus(tableId, ownerId, req));
    }

    // DELETE remove a table (owner only)
    @DeleteMapping("/tables/{tableId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteTable(
            @PathVariable String tableId,
            @AuthenticationPrincipal String ownerId) {
        tableService.deleteTable(tableId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
