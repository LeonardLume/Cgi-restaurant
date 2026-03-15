package com.example.restaurantbooking.repository;

import com.example.restaurantbooking.entity.Reservation;
import com.example.restaurantbooking.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByTableId(Long tableId);

    List<Reservation> findByTableIdAndStatus(Long tableId, ReservationStatus status);
    
        @Query("""
                        SELECT r
                        FROM Reservation r
                        WHERE r.tableId = :tableId
                            AND r.status = :status
                            AND r.reservationStart < :endTime
                            AND r.reservationEnd > :startTime
                        """)
    List<Reservation> findConflictingReservations(
        @Param("tableId") Long tableId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("status") ReservationStatus status
    );
    
        @Query("""
                        SELECT r
                        FROM Reservation r
                        WHERE r.status = :status
                            AND r.reservationStart < :endTime
                            AND r.reservationEnd > :startTime
                        """)
    List<Reservation> findActiveReservationsInTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("status") ReservationStatus status
    );
}
