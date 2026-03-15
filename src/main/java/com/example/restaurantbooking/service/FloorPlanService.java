package com.example.restaurantbooking.service;

import com.example.restaurantbooking.dto.TableDto;
import com.example.restaurantbooking.entity.ReservationStatus;
import com.example.restaurantbooking.entity.RestaurantTable;
import com.example.restaurantbooking.repository.ReservationRepository;
import com.example.restaurantbooking.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FloorPlanService {
    
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    
    /**
     * Get floor plan with table statuses for given time
     */
    public List<TableDto> getFloorPlan(LocalDateTime startTime, LocalDateTime endTime) {
        List<RestaurantTable> tables = tableRepository.findAll();
        
        return tables.stream()
            .map(table -> {
                TableDto dto = mapTableToDto(table);
                
                // Determine status based on reservations
                boolean hasConflict = !reservationRepository.findConflictingReservations(
                    table.getId(), startTime, endTime, ReservationStatus.ACTIVE
                ).isEmpty();
                
                dto.setStatus(hasConflict ? "OCCUPIED" : "AVAILABLE");
                
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Map RestaurantTable entity to DTO
     */
    private TableDto mapTableToDto(RestaurantTable table) {
        return new TableDto(
            table.getId(),
            table.getName(),
            table.getCapacity(),
            table.getZone(),
            table.getX(),
            table.getY(),
            table.getWidth(),
            table.getHeight(),
            table.getNearWindow(),
            table.getQuiet(),
            table.getAccessible(),
            table.getNearKidsArea(),
            table.getCombinableGroupId(),
            "AVAILABLE"
        );
    }
    
    /**
     * Get all tables
     */
    public List<TableDto> getAllTables() {
        return tableRepository.findAll().stream()
            .map(this::mapTableToDto)
            .collect(Collectors.toList());
    }
}
