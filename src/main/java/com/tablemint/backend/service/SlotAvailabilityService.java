package com.tablemint.backend.service;

import com.tablemint.backend.dto.response.SlotAvailabilityResponse;
import com.tablemint.backend.entity.DiningTable;
import com.tablemint.backend.repository.DiningTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotAvailabilityService {

    private final DiningTableRepository tableRepository;

    public List<SlotAvailabilityResponse> getAvailableSlots(
            String restaurantId,
            LocalDate date,
            int partySize,
            LocalTime openTime,
            LocalTime closeTime) {

        List<SlotAvailabilityResponse> slots = new ArrayList<>();
        LocalTime slot = openTime;

        while (slot.isBefore(closeTime.minusMinutes(30))) {
            List<DiningTable> available = tableRepository
                    .findAvailableTables(restaurantId, partySize, date, slot);
            slots.add(new SlotAvailabilityResponse(slot, available.size(), !available.isEmpty()));
            slot = slot.plusMinutes(30);
        }

        return slots;
    }
}
