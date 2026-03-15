package com.example.restaurantbooking.service;

import com.example.restaurantbooking.dto.ReservationRequest;
import com.example.restaurantbooking.dto.ReservationResponse;
import com.example.restaurantbooking.entity.Reservation;
import com.example.restaurantbooking.entity.ReservationStatus;
import com.example.restaurantbooking.entity.RestaurantTable;
import com.example.restaurantbooking.exception.ConflictException;
import com.example.restaurantbooking.exception.ResourceNotFoundException;
import com.example.restaurantbooking.repository.ReservationRepository;
import com.example.restaurantbooking.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final AvailabilityService availabilityService;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Optional<RestaurantTable> table = tableRepository.findById(request.getTableId());
        if (table.isEmpty()) {
            log.warn("Lauda ID-ga {} ei leitud", request.getTableId());
            throw new ResourceNotFoundException("Lauda ei leitud");
        }

        RestaurantTable restaurantTable = table.get();

        if (restaurantTable.getCapacity() < request.getPartySize()) {
            log.warn(
                    "Laua mahutavus on liiga väike. TableId={}, capacity={}, requestedPartySize={}",
                    request.getTableId(),
                    restaurantTable.getCapacity(),
                    request.getPartySize()
            );
            throw new ConflictException("Laua mahutavus on liiga väike");
        }

        if (!availabilityService.isTableAvailable(
                request.getTableId(),
                request.getStartTime(),
                request.getEndTime()
        )) {
            log.info("Laud {} pole saadaval: {} - {}",
                    request.getTableId(), request.getStartTime(), request.getEndTime());
            throw new ConflictException("Laud sellel ajal ei ole saadaval");
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerName(request.getCustomerName());
        reservation.setTableId(request.getTableId());
        reservation.setPartySize(request.getPartySize());
        reservation.setReservationStart(request.getStartTime());
        reservation.setReservationEnd(request.getEndTime());
        reservation.setStatus(ReservationStatus.ACTIVE);

        Reservation saved = reservationRepository.save(reservation);
        log.info("Broneering loodud: id={}, laud={}, klient={}",
                saved.getId(), saved.getTableId(), saved.getCustomerName());

        return new ReservationResponse(saved.getId(), saved.getCustomerName(),
                saved.getTableId(), saved.getReservationStart(), saved.getReservationEnd(),
                saved.getPartySize(), saved.getStatus(), "Broneering edukalt loodud");
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (reservation.isEmpty()) {
            log.warn("Broneeringut ID-ga {} ei leitud", reservationId);
            throw new ResourceNotFoundException("Broneeringut ei leitud");
        }

        Reservation r = reservation.get();
        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
        log.info("Broneering tühistatud: id={}", reservationId);

        return new ReservationResponse(r.getId(), r.getCustomerName(), r.getTableId(),
                r.getReservationStart(), r.getReservationEnd(), r.getPartySize(),
                r.getStatus(), "Broneering tühistatud");
    }
}
