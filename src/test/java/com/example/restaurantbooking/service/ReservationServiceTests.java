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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TableRepository tableRepository;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationRequest request;
    private RestaurantTable table;

    @BeforeEach
    void setUp() {
        request = new ReservationRequest(
                "Leona",
                1L,
                4,
                LocalDateTime.of(2026, 3, 20, 18, 0),
                LocalDateTime.of(2026, 3, 20, 20, 0)
        );

        table = new RestaurantTable();
        table.setId(1L);
        table.setName("T1");
        table.setCapacity(4);
    }

    @Test
    void createReservationReturnsSavedReservationWhenRequestIsValid() {
        Reservation savedReservation = new Reservation();
        savedReservation.setId(10L);
        savedReservation.setCustomerName(request.getCustomerName());
        savedReservation.setTableId(request.getTableId());
        savedReservation.setPartySize(request.getPartySize());
        savedReservation.setReservationStart(request.getStartTime());
        savedReservation.setReservationEnd(request.getEndTime());
        savedReservation.setStatus(ReservationStatus.ACTIVE);

        when(tableRepository.findById(request.getTableId())).thenReturn(Optional.of(table));
        when(availabilityService.isTableAvailable(request.getTableId(), request.getStartTime(), request.getEndTime()))
                .thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponse response = reservationService.createReservation(request);

        assertEquals(10L, response.getId());
        assertEquals(ReservationStatus.ACTIVE, response.getStatus());
        assertEquals("Broneering edukalt loodud", response.getMessage());
    }

    @Test
    void createReservationThrowsWhenTableDoesNotExist() {
        when(tableRepository.findById(request.getTableId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request)
        );

        assertEquals("Lauda ei leitud", exception.getMessage());
    }

    @Test
    void createReservationThrowsWhenTableCapacityIsTooSmall() {
        table.setCapacity(2);
        when(tableRepository.findById(request.getTableId())).thenReturn(Optional.of(table));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> reservationService.createReservation(request)
        );

        assertEquals("Laua mahutavus on liiga väike", exception.getMessage());
    }

    @Test
    void createReservationThrowsWhenTableIsUnavailable() {
        when(tableRepository.findById(request.getTableId())).thenReturn(Optional.of(table));
        when(availabilityService.isTableAvailable(request.getTableId(), request.getStartTime(), request.getEndTime()))
                .thenReturn(false);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> reservationService.createReservation(request)
        );

        assertEquals("Laud sellel ajal ei ole saadaval", exception.getMessage());
    }

    @Test
    void cancelReservationThrowsWhenReservationDoesNotExist() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.cancelReservation(99L)
        );

        assertEquals("Broneeringut ei leitud", exception.getMessage());
    }

    @Test
    void cancelReservationReturnsCancelledReservationWhenItExists() {
        Reservation reservation = new Reservation();
        reservation.setId(15L);
        reservation.setCustomerName("Leona");
        reservation.setTableId(1L);
        reservation.setPartySize(4);
        reservation.setReservationStart(request.getStartTime());
        reservation.setReservationEnd(request.getEndTime());
        reservation.setStatus(ReservationStatus.ACTIVE);

        when(reservationRepository.findById(15L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationService.cancelReservation(15L);

        assertEquals(15L, response.getId());
        assertEquals(ReservationStatus.CANCELLED, response.getStatus());
        assertTrue(response.getMessage().contains("tühistatud"));
    }
}