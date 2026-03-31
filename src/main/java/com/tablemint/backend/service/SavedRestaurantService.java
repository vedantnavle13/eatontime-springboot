package com.tablemint.backend.service;

import com.tablemint.backend.dto.response.RestaurantSummaryResponse;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.RestaurantRepository;
import com.tablemint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedRestaurantService {

    private final UserRepository       userRepository;
    private final RestaurantRepository restaurantRepository;

    public void save(String userId, String restaurantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        if (!user.getSavedRestaurants().contains(restaurant)) {
            user.getSavedRestaurants().add(restaurant);
            userRepository.save(user);
        }
    }

    public void unsave(String userId, String restaurantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        user.getSavedRestaurants().remove(restaurant);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<RestaurantSummaryResponse> getSaved(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return user.getSavedRestaurants().stream()
                .map(r -> new RestaurantSummaryResponse(
                        r.getId(), r.getName(), r.getLocality(), r.getCity(),
                        r.getCoverImageUrl(), r.getAverageRating(), r.getTotalReviews(),
                        r.getAverageCostForTwo(), r.getCuisines()))
                .toList();
    }
}
