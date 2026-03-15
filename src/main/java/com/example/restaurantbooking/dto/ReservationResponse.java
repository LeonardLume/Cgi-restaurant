package com.example.restaurantbooking.dto;

import com.example.restaurantbooking.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String customerName;
    private Long tableId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer partySize;
    private ReservationStatus status;
    private String message;
}
