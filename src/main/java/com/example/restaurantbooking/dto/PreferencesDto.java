package com.example.restaurantbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesDto {
    private Boolean quiet = false;
    private Boolean nearWindow = false;
    private Boolean accessible = false;
    private Boolean nearKidsArea = false;
}
