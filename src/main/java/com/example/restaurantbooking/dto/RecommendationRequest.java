package com.example.restaurantbooking.dto;

import com.example.restaurantbooking.entity.Zone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

    @NotNull(message = "Kuupäev ja kellaaeg on kohustuslikud")
    private LocalDateTime dateTime;

    @NotNull(message = "Seltskonna suurus on kohustuslik")
    @Min(value = 1, message = "Seltskonna suurus peab olema vähemalt 1")
    private Integer partySize;

    private Zone zone;

    @Valid
    private PreferencesDto preferences;

    @Min(value = 15, message = "Broneeringu kestus peab olema vähemalt 15 minutit")
    @Max(value = 480, message = "Broneeringu kestus peab olema kuni 480 minutit")
    private Integer durationMinutes = 120;
}
