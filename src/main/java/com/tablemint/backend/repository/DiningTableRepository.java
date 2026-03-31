package com.tablemint.backend.repository;

import com.tablemint.backend.entity.DiningTable;
import com.tablemint.backend.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DiningTableRepository extends JpaRepository<DiningTable, String> {

    List<DiningTable> findByRestaurantIdAndStatus(String restaurantId, TableStatus status);

    @Query("""
            SELECT t FROM DiningTable t
            WHERE t.restaurant.id = :restaurantId
            AND t.capacity >= :partySize
            AND t.status = 'AVAILABLE'
            AND t.id NOT IN (
                SELECT res.table.id FROM Reservation res
                WHERE res.reservationDate = :date
                AND res.slotTime = :slot
                AND res.status IN ('PENDING','CONFIRMED','SEATED')
            )
            """)
    List<DiningTable> findAvailableTables(
            @Param("restaurantId") String restaurantId,
            @Param("partySize") int partySize,
            @Param("date") LocalDate date,
            @Param("slot") LocalTime slot);
}