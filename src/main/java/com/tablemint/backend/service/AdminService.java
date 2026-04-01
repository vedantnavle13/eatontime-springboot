package com.tablemint.backend.service;

import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.enums.UserRole;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.ReservationRepository;
import com.tablemint.backend.repository.RestaurantRepository;
import com.tablemint.backend.repository.ReviewRepository;
import com.tablemint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final RestaurantRepository  restaurantRepository;
    private final UserRepository        userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository      reviewRepository;

    // ── Restaurants ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant verifyRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
        restaurant.setActive(true);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant deactivateRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
        restaurant.setActive(false);
        return restaurantRepository.save(restaurant);
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User banUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setActive(false);
        return userRepository.save(user);
    }

    public User unbanUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setActive(true);
        return userRepository.save(user);
    }

    public User promoteToAdmin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user);
    }

    // ── Platform stats ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRestaurants", restaurantRepository.count());
        stats.put("activeRestaurants",
                restaurantRepository.findTopRated(PageRequest.of(0, Integer.MAX_VALUE)).size());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalReservations", reservationRepository.count());
        stats.put("totalReviews", reviewRepository.count());
        return stats;
    }
}