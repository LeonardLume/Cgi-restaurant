package com.example.restaurantbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorPlanResponse {
    private List<TableDto> tables;
    private Integer totalTables;
    private Integer availableTables;
}
