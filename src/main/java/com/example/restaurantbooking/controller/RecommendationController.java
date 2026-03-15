package com.example.restaurantbooking.controller;

import com.example.restaurantbooking.dto.RecommendationRequest;
import com.example.restaurantbooking.dto.RecommendationResponse;
import com.example.restaurantbooking.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Valid @RequestBody RecommendationRequest request) {
        RecommendationResponse response = recommendationService.recommendTables(request);
        return ResponseEntity.ok(response);
    }
}
