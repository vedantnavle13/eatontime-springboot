package com.tablemint.backend.repository;

import com.tablemint.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findByRestaurantIdOrderByCreatedAtDesc(String restaurantId);

    boolean existsByReservationId(String reservationId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double calculateAverageRating(@Param("restaurantId") String restaurantId);
}
