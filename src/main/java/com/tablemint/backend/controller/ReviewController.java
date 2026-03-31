package com.tablemint.backend.controller;

import com.tablemint.backend.dto.request.CreateReviewRequest;
import com.tablemint.backend.entity.Review;
import com.tablemint.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateReviewRequest req) {
        return ResponseEntity.status(201).body(reviewService.createReview(userId, req));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Review>> byRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(reviewService.getByRestaurant(restaurantId));
    }
}