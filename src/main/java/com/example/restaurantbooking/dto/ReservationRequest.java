package com.example.restaurantbooking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotBlank(message = "Kliendi nimi on kohustuslik")
    private String customerName;

    @NotNull(message = "Laua ID on kohustuslik")
    private Long tableId;

    @NotNull(message = "Seltskonna suurus on kohustuslik")
    @Min(value = 1, message = "Seltskonna suurus peab olema vähemalt 1")
    private Integer partySize;

    @NotNull(message = "Algusaeg on kohustuslik")
    private LocalDateTime startTime;

    @NotNull(message = "Lõppaeg on kohustuslik")
    private LocalDateTime endTime;

    @AssertTrue(message = "Broneeringu lõppaeg peab olema hilisem kui algusaeg")
    public boolean isTimeRangeValid() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }
}
