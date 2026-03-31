package com.tablemint.backend.controller;

import com.tablemint.backend.dto.request.CreateReservationRequest;
import com.tablemint.backend.dto.response.ReservationResponse;
import com.tablemint.backend.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateReservationRequest req) {
        return ResponseEntity.status(201).body(reservationService.create(userId, req));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> myReservations(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(reservationService.getMyReservations(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancel(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        return ResponseEntity.ok(reservationService.cancel(id, userId));
    }

    @PostMapping("/scan/{qrToken}")
    @PreAuthorize("hasRole('CAPTAIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ReservationResponse> scanQr(@PathVariable String qrToken) {
        return ResponseEntity.ok(reservationService.scanQr(qrToken));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('CAPTAIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ReservationResponse> complete(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.complete(id));
    }
}