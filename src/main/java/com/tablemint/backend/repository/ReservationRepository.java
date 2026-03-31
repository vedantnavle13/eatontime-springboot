package com.tablemint.backend.repository;

import com.tablemint.backend.entity.Reservation;
import com.tablemint.backend.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Reservation> findByRestaurantIdAndReservationDateAndStatus(
            String restaurantId, LocalDate date, ReservationStatus status);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.table.id = :tableId
            AND r.reservationDate = :date
            AND r.status IN ('PENDING','CONFIRMED','SEATED')
            """)
    List<Reservation> findActiveByTableAndDate(
            @Param("tableId") String tableId,
            @Param("date") LocalDate date);

    Optional<Reservation> findByQrCodeToken(String token);
}
