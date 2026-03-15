package com.example.restaurantbooking.dto;

import com.example.restaurantbooking.entity.Zone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDto {
    private Long id;
    private String name;
    private Integer capacity;
    private Zone zone;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Boolean nearWindow;
    private Boolean quiet;
    private Boolean accessible;
    private Boolean nearKidsArea;
    private Long combinableGroupId;
    private String status; // AVAILABLE, OCCUPIED, RECOMMENDED
}
