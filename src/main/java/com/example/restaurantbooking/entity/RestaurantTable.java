package com.example.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Zone zone;
    
    @Column(nullable = false)
    private Integer x;
    
    @Column(nullable = false)
    private Integer y;
    
    @Column(nullable = false)
    private Integer width;
    
    @Column(nullable = false)
    private Integer height;
    
    @Column(name = "near_window")
    private Boolean nearWindow = false;
    
    @Column(name = "quiet")
    private Boolean quiet = false;
    
    @Column(name = "accessible")
    private Boolean accessible = false;
    
    @Column(name = "near_kids_area")
    private Boolean nearKidsArea = false;
    
    @Column(name = "combinable_group_id")
    private Long combinableGroupId;
}
