package com.example.restaurantbooking.service;

import com.example.restaurantbooking.dto.RecommendationRequest;
import com.example.restaurantbooking.dto.RecommendationResponse;
import com.example.restaurantbooking.dto.TableRecommendationDto;
import com.example.restaurantbooking.entity.RestaurantTable;
import com.example.restaurantbooking.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final TableRepository tableRepository;
    private final AvailabilityService availabilityService;
    private final ScoringService scoringService;

    public RecommendationResponse recommendTables(RecommendationRequest request) {
        LocalDateTime startTime = request.getDateTime();
        int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 120;
        LocalDateTime endTime = startTime.plusMinutes(duration);

        List<RestaurantTable> candidates = getCandidateTables(request, startTime, endTime);

        if (candidates.isEmpty()) {
            log.info("Sobivaid laudu ei leitud: partySize={}, zone={}, aeg={}",
                    request.getPartySize(), request.getZone(), startTime);
            return new RecommendationResponse(null, null, List.of(),
                    "Sellel ajal ei ole sobivaid laudu saadaval");
        }

        List<TableRecommendationDto> scored = scoreTables(candidates, request);
        TableRecommendationDto best = scored.get(0);

        log.info("Leitud {} soovitust, parim: {} (skoor {})",
                scored.size(), best.getTableName(), best.getScore());

        return new RecommendationResponse(
                best.getTableId(),
                best.getTableName(),
                scored,
                "Leitud " + scored.size() + " soovitust"
        );
    }

    public Optional<TableRecommendationDto> findBestTable(RecommendationRequest request) {
        RecommendationResponse response = recommendTables(request);
        return response.getRecommendedTables().stream().findFirst();
    }

    private List<RestaurantTable> getCandidateTables(RecommendationRequest request,
                                                     LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        List<RestaurantTable> tables;

        if (request.getZone() != null) {
            tables = tableRepository.findByZoneAndCapacityGreaterThanEqual(
                    request.getZone(), request.getPartySize());
        } else {
            tables = tableRepository.findByCapacityGreaterThanEqual(request.getPartySize());
        }

        return tables.stream()
                .filter(t -> availabilityService.isTableAvailable(t.getId(), startTime, endTime))
                .collect(Collectors.toList());
    }

    private List<TableRecommendationDto> scoreTables(List<RestaurantTable> tables,
                                                     RecommendationRequest request) {
        return tables.stream()
                .map(table -> {
                    int score = scoringService.calculateScore(
                            table, request.getPartySize(), request.getPreferences());
                    String reason = scoringService.generateExplanation(
                            table, request.getPartySize(), request.getPreferences());
                    return new TableRecommendationDto(table.getId(), table.getName(), score, reason);
                })
                .sorted(Comparator.comparingInt(TableRecommendationDto::getScore).reversed())
                .collect(Collectors.toList());
    }
}
