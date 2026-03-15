package com.example.restaurantbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private Long bestTableId;
    private String bestTableName;
    private List<TableRecommendationDto> recommendedTables;
    private String message;
}
