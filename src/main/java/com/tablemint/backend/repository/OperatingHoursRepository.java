package com.tablemint.backend.repository;

import com.tablemint.backend.entity.OperatingHours;
import com.tablemint.backend.enums.OperatingDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperatingHoursRepository extends JpaRepository<OperatingHours, String> {

    List<OperatingHours> findByRestaurantId(String restaurantId);

    Optional<OperatingHours> findByRestaurantIdAndDay(String restaurantId, OperatingDay day);
}