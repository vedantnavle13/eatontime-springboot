package com.tablemint.backend.service;

import com.tablemint.backend.dto.request.CreateDiningTableRequest;
import com.tablemint.backend.dto.request.UpdateTableStatusRequest;
import com.tablemint.backend.entity.DiningTable;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.exception.ForbiddenException;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.DiningTableRepository;
import com.tablemint.backend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiningTableService {

    private final DiningTableRepository tableRepository;
    private final RestaurantRepository  restaurantRepository;

    public DiningTable addTable(String restaurantId, String ownerId,
                                CreateDiningTableRequest req) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Not your restaurant");
        }

        DiningTable table = new DiningTable();
        table.setRestaurant(restaurant);
        table.setTableNumber(req.tableNumber());
        table.setCapacity(req.capacity());
        table.setOutdoor(req.isOutdoor());
        table.setAccessible(req.isAccessible());

        return tableRepository.save(table);
    }

    public DiningTable updateStatus(String tableId, String ownerId,
                                    UpdateTableStatusRequest req) {
        DiningTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table not found"));

        if (!table.getRestaurant().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Not your restaurant");
        }

        table.setStatus(req.status());
        return tableRepository.save(table);
    }

    @Transactional(readOnly = true)
    public List<DiningTable> getByRestaurant(String restaurantId) {
        return tableRepository.findByRestaurantIdAndStatus(
                restaurantId,
                com.tablemint.backend.enums.TableStatus.AVAILABLE);
    }

    public void deleteTable(String tableId, String ownerId) {
        DiningTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table not found"));

        if (!table.getRestaurant().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Not your restaurant");
        }

        tableRepository.delete(table);
    }
}
