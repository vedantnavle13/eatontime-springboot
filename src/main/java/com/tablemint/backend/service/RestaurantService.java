package com.tablemint.backend.service;

import com.tablemint.backend.dto.request.CreateRestaurantRequest;
import com.tablemint.backend.dto.response.RestaurantSummaryResponse;
import com.tablemint.backend.dto.response.SlotAvailabilityResponse;
import com.tablemint.backend.entity.OperatingHours;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.enums.CuisineType;
import com.tablemint.backend.enums.OperatingDay;
import com.tablemint.backend.exception.ForbiddenException;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.OperatingHoursRepository;
import com.tablemint.backend.repository.RestaurantRepository;
import com.tablemint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository     restaurantRepository;
    private final UserRepository           userRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final SlotAvailabilityService  slotAvailabilityService;

    public List<RestaurantSummaryResponse> search(String city, String locality,
                                                  CuisineType cuisine, int page) {
        List<Restaurant> results;

        if (cuisine != null) {
            results = restaurantRepository.findByCuisine(cuisine);
        } else if (locality != null && !locality.isBlank()) {
            results = restaurantRepository.findByLocalityIgnoreCaseAndActiveTrue(locality);
        } else if (city != null && !city.isBlank()) {
            results = restaurantRepository.findByCityIgnoreCaseAndActiveTrue(city);
        } else {
            results = restaurantRepository.findTopRated(PageRequest.of(page, 20));
        }

        return results.stream().map(this::toSummary).toList();
    }

    public Restaurant findById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + id));
    }

    public List<SlotAvailabilityResponse> getSlots(String restaurantId,
                                                   LocalDate date, int partySize) {
        findById(restaurantId);

        String dayName = date.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();

        OperatingHours hours = operatingHoursRepository
                .findByRestaurantIdAndDay(restaurantId, OperatingDay.valueOf(dayName))
                .orElseThrow(() -> new NotFoundException(
                        "No operating hours configured for " + dayName));

        if (hours.isClosed()) {
            return List.of();
        }

        return slotAvailabilityService.getAvailableSlots(
                restaurantId, date, partySize, hours.getOpenTime(), hours.getCloseTime());
    }

    @Transactional
    public Restaurant create(String ownerId, CreateRestaurantRequest req) {
        User owner = userRepository.getReferenceById(ownerId);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(req.name());
        restaurant.setDescription(req.description());
        restaurant.setAddress(req.address());
        restaurant.setCity(req.city());
        restaurant.setLocality(req.locality());
        restaurant.setPhone(req.phone());
        restaurant.setOwner(owner);
        restaurant.setAverageCostForTwo(req.averageCostForTwo());
        if (req.cuisines() != null) {
            restaurant.setCuisines(req.cuisines());
        }

        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant update(String restaurantId, String ownerId, CreateRestaurantRequest req) {
        Restaurant restaurant = findById(restaurantId);

        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Not your restaurant");
        }

        restaurant.setName(req.name());
        restaurant.setDescription(req.description());
        restaurant.setAddress(req.address());
        restaurant.setCity(req.city());
        restaurant.setLocality(req.locality());
        restaurant.setPhone(req.phone());
        restaurant.setAverageCostForTwo(req.averageCostForTwo());
        if (req.cuisines() != null) {
            restaurant.setCuisines(req.cuisines());
        }

        return restaurantRepository.save(restaurant);
    }

    private RestaurantSummaryResponse toSummary(Restaurant r) {
        return new RestaurantSummaryResponse(
                r.getId(), r.getName(), r.getLocality(), r.getCity(),
                r.getCoverImageUrl(), r.getAverageRating(), r.getTotalReviews(),
                r.getAverageCostForTwo(), r.getCuisines());
    }
}
