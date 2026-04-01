package com.tablemint.backend.controller;

import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ── Platform stats ────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    // ── Restaurants ───────────────────────────────────────────────────────────

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(adminService.getAllRestaurants());
    }

    @PatchMapping("/restaurants/{id}/verify")
    public ResponseEntity<Restaurant> verifyRestaurant(@PathVariable String id) {
        return ResponseEntity.ok(adminService.verifyRestaurant(id));
    }

    @PatchMapping("/restaurants/{id}/deactivate")
    public ResponseEntity<Restaurant> deactivateRestaurant(@PathVariable String id) {
        return ResponseEntity.ok(adminService.deactivateRestaurant(id));
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<User> banUser(@PathVariable String id) {
        return ResponseEntity.ok(adminService.banUser(id));
    }

    @PatchMapping("/users/{id}/unban")
    public ResponseEntity<User> unbanUser(@PathVariable String id) {
        return ResponseEntity.ok(adminService.unbanUser(id));
    }

    @PatchMapping("/users/{id}/promote-admin")
    public ResponseEntity<User> promoteToAdmin(@PathVariable String id) {
        return ResponseEntity.ok(adminService.promoteToAdmin(id));
    }
}