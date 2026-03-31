package com.tablemint.backend.service;

import com.tablemint.backend.dto.request.CreateReviewRequest;
import com.tablemint.backend.entity.Reservation;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.entity.Review;
import com.tablemint.backend.enums.ReservationStatus;
import com.tablemint.backend.exception.ConflictException;
import com.tablemint.backend.exception.ForbiddenException;
import com.tablemint.backend.exception.InvalidStateException;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.ReservationRepository;
import com.tablemint.backend.repository.RestaurantRepository;
import com.tablemint.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository      reviewRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository  restaurantRepository;

    public Review createReview(String userId, CreateReviewRequest req) {
        Reservation reservation = reservationRepository.findById(req.reservationId())
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Not your reservation");
        }

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new InvalidStateException("You can only review after dining is completed");
        }

        if (reviewRepository.existsByReservationId(req.reservationId())) {
            throw new ConflictException("Review already submitted for this reservation");
        }

        Review review = new Review();
        review.setReservation(reservation);
        review.setRestaurant(reservation.getRestaurant());
        review.setUser(reservation.getUser());
        review.setRating(req.rating());
        review.setComment(req.comment());
        reviewRepository.save(review);

        Restaurant restaurant = reservation.getRestaurant();
        Double avg = reviewRepository.calculateAverageRating(restaurant.getId());
        if (avg != null) {
            restaurant.setAverageRating(Math.round(avg * 10.0) / 10.0);
        }
        restaurant.setTotalReviews(restaurant.getTotalReviews() + 1);
        restaurantRepository.save(restaurant);

        return review;
    }

    @Transactional(readOnly = true)
    public List<Review> getByRestaurant(String restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }
}