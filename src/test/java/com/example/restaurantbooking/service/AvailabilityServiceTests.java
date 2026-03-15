package com.example.restaurantbooking.service;

import com.example.restaurantbooking.entity.Reservation;
import com.example.restaurantbooking.entity.ReservationStatus;
import com.example.restaurantbooking.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceTests {
    
    @Mock
    private ReservationRepository reservationRepository;
    
    private AvailabilityService availabilityService;
    
    @BeforeEach
    public void setUp() {
        availabilityService = new AvailabilityService(reservationRepository);
    }
    
    @Test
    public void testTableAvailableWhenNoReservations() {
        Long tableId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 3, 20, 19, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 20, 21, 0);
        
        when(reservationRepository.findConflictingReservations(
            tableId, start, end, ReservationStatus.ACTIVE
        )).thenReturn(new ArrayList<>());
        
        assertTrue(availabilityService.isTableAvailable(tableId, start, end));
    }
    
    @Test
    public void testTableNotAvailableWithConflict() {
        Long tableId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 3, 20, 19, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 20, 21, 0);
        
        Reservation conflict = new Reservation();
        conflict.setTableId(tableId);
        conflict.setReservationStart(LocalDateTime.of(2026, 3, 20, 20, 0));
        conflict.setReservationEnd(LocalDateTime.of(2026, 3, 20, 22, 0));
        
        List<Reservation> conflicts = new ArrayList<>();
        conflicts.add(conflict);
        
        when(reservationRepository.findConflictingReservations(
            tableId, start, end, ReservationStatus.ACTIVE
        )).thenReturn(conflicts);
        
        assertFalse(availabilityService.isTableAvailable(tableId, start, end));
    }
}
