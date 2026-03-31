package com.tablemint.backend.service;

import com.tablemint.backend.dto.request.CreateReservationRequest;
import com.tablemint.backend.dto.response.ReservationResponse;
import com.tablemint.backend.entity.DiningTable;
import com.tablemint.backend.entity.Reservation;
import com.tablemint.backend.entity.Restaurant;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.enums.ReservationStatus;
import com.tablemint.backend.enums.TableStatus;
import com.tablemint.backend.exception.InvalidStateException;
import com.tablemint.backend.exception.NoAvailabilityException;
import com.tablemint.backend.exception.NotFoundException;
import com.tablemint.backend.repository.DiningTableRepository;
import com.tablemint.backend.repository.ReservationRepository;
import com.tablemint.backend.repository.RestaurantRepository;
import com.tablemint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DiningTableRepository tableRepository;
    private final RestaurantRepository  restaurantRepository;
    private final UserRepository        userRepository;

    public ReservationResponse create(String userId, CreateReservationRequest req) {
        Restaurant restaurant = restaurantRepository.findById(req.restaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        List<DiningTable> available = tableRepository.findAvailableTables(
                req.restaurantId(), req.partySize(), req.reservationDate(), req.slotTime());

        if (available.isEmpty()) {
            throw new NoAvailabilityException("No tables available for the selected slot");
        }

        User user = userRepository.getReferenceById(userId);
        DiningTable table = available.get(0);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRestaurant(restaurant);
        reservation.setTable(table);
        reservation.setReservationDate(req.reservationDate());
        reservation.setSlotTime(req.slotTime());
        reservation.setPartySize(req.partySize());
        reservation.setSpecialRequests(req.specialRequests());
        reservation.setQrCodeToken(UUID.randomUUID().toString());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        table.setStatus(TableStatus.RESERVED);
        tableRepository.save(table);

        return toResponse(reservation);
    }

    public ReservationResponse scanQr(String qrToken) {
        Reservation reservation = reservationRepository.findByQrCodeToken(qrToken)
                .orElseThrow(() -> new NotFoundException("Invalid QR code"));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidStateException("Reservation is not in CONFIRMED state");
        }

        reservation.setStatus(ReservationStatus.SEATED);
        reservation.setQrScannedAt(LocalDateTime.now());
        reservation.getTable().setStatus(TableStatus.OCCUPIED);
        return toResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse complete(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.getTable().setStatus(TableStatus.AVAILABLE);
        return toResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse cancel(String reservationId, String userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new com.tablemint.backend.exception.ForbiddenException("Not your reservation");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED
                || reservation.getStatus() == ReservationStatus.SEATED) {
            throw new InvalidStateException("Cannot cancel a reservation that is already seated or completed");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.getTable().setStatus(TableStatus.AVAILABLE);
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(String userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getById(String reservationId) {
        return toResponse(reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found")));
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getRestaurant().getName(),
                r.getRestaurant().getId(),
                r.getReservationDate(),
                r.getSlotTime(),
                r.getPartySize(),
                r.getStatus(),
                r.getQrCodeToken(),
                r.getCreatedAt());
    }
}
