package com.example.restaurantbooking.service;

import com.example.restaurantbooking.entity.Reservation;
import com.example.restaurantbooking.entity.ReservationStatus;
import com.example.restaurantbooking.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ReservationRepository reservationRepository;

    public boolean isTableAvailable(Long tableId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Reservation> conflicting = reservationRepository.findConflictingReservations(
                tableId, startTime, endTime, ReservationStatus.ACTIVE
        );
        return conflicting.isEmpty();
    }

    public List<Reservation> getTableReservations(Long tableId) {
        return reservationRepository.findByTableIdAndStatus(tableId, ReservationStatus.ACTIVE);
    }
}
