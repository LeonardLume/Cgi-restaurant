package com.example.restaurantbooking.repository;

import com.example.restaurantbooking.entity.RestaurantTable;
import com.example.restaurantbooking.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByZone(Zone zone);
    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);
    List<RestaurantTable> findByZoneAndCapacityGreaterThanEqual(Zone zone, Integer capacity);
}
