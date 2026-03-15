package com.example.restaurantbooking.controller;

import com.example.restaurantbooking.dto.FloorPlanResponse;
import com.example.restaurantbooking.dto.TableDto;
import com.example.restaurantbooking.service.FloorPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/floor-plan")
@RequiredArgsConstructor
public class FloorPlanController {
    
    private final FloorPlanService floorPlanService;
    
    /**
     * Get floor plan with table statuses
     */
    @GetMapping
    public ResponseEntity<FloorPlanResponse> getFloorPlan(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (endTime == null) {
            endTime = startTime.plusHours(2);
        }
        
        List<TableDto> tables = floorPlanService.getFloorPlan(startTime, endTime);
        
        FloorPlanResponse response = new FloorPlanResponse();
        response.setTables(tables);
        response.setTotalTables(tables.size());
        response.setAvailableTables((int) tables.stream().filter(t -> "AVAILABLE".equals(t.getStatus())).count());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all tables
     */
    @GetMapping("/all")
    public ResponseEntity<List<TableDto>> getAllTables() {
        return ResponseEntity.ok(floorPlanService.getAllTables());
    }
}
