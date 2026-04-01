package com.tablemint.backend.service;

import com.tablemint.backend.dto.request.SetOperatingHoursRequest;
import com.tablemint.backend.entity.OperatingHours;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.exception.ForbiddenException;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.OperatingHoursRepository;
import com.tablemint.backend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OperatingHoursService {

    private final OperatingHoursRepository hoursRepository;
    private final RestaurantRepository     restaurantRepository;

    // Upsert — creates or updates hours for a given day
    public OperatingHours setHours(String restaurantId, String ownerId,
                                   SetOperatingHoursRequest req) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Not your restaurant");
        }

        // Find existing record for this day, or create new
        OperatingHours hours = hoursRepository
                .findByRestaurantIdAndDay(restaurantId, req.day())
                .orElse(new OperatingHours());

        hours.setRestaurant(restaurant);
        hours.setDay(req.day());
        hours.setClosed(req.closed());

        if (!req.closed()) {
            if (req.openTime() == null || req.closeTime() == null) {
                throw new com.tablemint.backend.exception.InvalidStateException(
                        "openTime and closeTime are required when closed=false");
            }
            hours.setOpenTime(req.openTime());
            hours.setCloseTime(req.closeTime());
        }

        return hoursRepository.save(hours);
    }

    @Transactional(readOnly = true)
    public List<OperatingHours> getByRestaurant(String restaurantId) {
        return hoursRepository.findByRestaurantId(restaurantId);
    }

    // Bulk setup — set all 7 days at once (useful for onboarding)
    public List<OperatingHours> setBulkHours(String restaurantId, String ownerId,
                                             List<SetOperatingHoursRequest> requests) {
        return requests.stream()
                .map(req -> setHours(restaurantId, ownerId, req))
                .toList();
    }
}
