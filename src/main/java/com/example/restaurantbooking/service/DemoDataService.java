package com.example.restaurantbooking.service;

import com.example.restaurantbooking.entity.Reservation;
import com.example.restaurantbooking.entity.ReservationStatus;
import com.example.restaurantbooking.entity.RestaurantTable;
import com.example.restaurantbooking.entity.Zone;
import com.example.restaurantbooking.repository.ReservationRepository;
import com.example.restaurantbooking.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DemoDataService {
    
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final Random random = new Random();
    
    /**
     * Initialize demo data with tables and random reservations
     */
    public void initializeDemoData() {
        // Clear existing data
        reservationRepository.deleteAll();
        tableRepository.deleteAll();
        
        // Create demo tables
        createDemoTables();
        
        // Create random reservations
        createRandomReservations();
    }
    
    /**
     * Create demo restaurant tables
     */
    private void createDemoTables() {
        // Main hall tables
        tableRepository.save(createTable("M1", 2, Zone.MAIN_HALL, 50, 100, false, true, false, false));
        tableRepository.save(createTable("M2", 2, Zone.MAIN_HALL, 150, 100, true, false, false, false));
        tableRepository.save(createTable("M3", 4, Zone.MAIN_HALL, 250, 100, false, false, false, false));
        tableRepository.save(createTable("M4", 4, Zone.MAIN_HALL, 350, 100, true, true, false, false));
        tableRepository.save(createTable("M5", 6, Zone.MAIN_HALL, 50, 250, false, false, true, false));
        tableRepository.save(createTable("M6", 6, Zone.MAIN_HALL, 150, 250, true, false, false, false));
        tableRepository.save(createTable("M7", 8, Zone.MAIN_HALL, 250, 250, false, true, false, false));
        tableRepository.save(createTable("M8", 8, Zone.MAIN_HALL, 350, 250, true, true, false, false));
        
        // Terrace tables
        tableRepository.save(createTable("T1", 2, Zone.TERRACE, 50, 400, true, false, false, false));
        tableRepository.save(createTable("T2", 4, Zone.TERRACE, 150, 400, true, false, false, false));
        tableRepository.save(createTable("T3", 6, Zone.TERRACE, 250, 400, true, false, false, false));
        tableRepository.save(createTable("T4", 8, Zone.TERRACE, 350, 400, true, true, false, false));
        
        // Private room
        tableRepository.save(createTable("P1", 10, Zone.PRIVATE_ROOM, 100, 550, false, true, true, false));
        tableRepository.save(createTable("P2", 12, Zone.PRIVATE_ROOM, 250, 550, false, true, true, false));
    }
    
    /**
     * Create table entity
     */
    private RestaurantTable createTable(String name, Integer capacity, Zone zone, 
                                        Integer x, Integer y, Boolean nearWindow, 
                                        Boolean quiet, Boolean accessible, Boolean nearKidsArea) {
        RestaurantTable table = new RestaurantTable();
        table.setName(name);
        table.setCapacity(capacity);
        table.setZone(zone);
        table.setX(x);
        table.setY(y);
        table.setWidth(80);
        table.setHeight(60);
        table.setNearWindow(nearWindow);
        table.setQuiet(quiet);
        table.setAccessible(accessible);
        table.setNearKidsArea(nearKidsArea);
        return table;
    }
    
    /**
     * Create random reservations for demo purposes
     */
    private void createRandomReservations() {
        List<RestaurantTable> tables = tableRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        // Create 5-8 random reservations spread across the day
        int reservationCount = 5 + random.nextInt(4);
        for (int i = 0; i < reservationCount; i++) {
            RestaurantTable table = tables.get(random.nextInt(tables.size()));
            LocalDateTime startTime = now.plusHours(random.nextInt(12)).withMinute(0).withSecond(0);
            LocalDateTime endTime = startTime.plusHours(2);
            
            Reservation reservation = new Reservation();
            reservation.setCustomerName("Külastaja " + (i + 1));
            reservation.setTableId(table.getId());
            reservation.setPartySize(Math.min(table.getCapacity(), 1 + random.nextInt(table.getCapacity())));
            reservation.setReservationStart(startTime);
            reservation.setReservationEnd(endTime);
            reservation.setStatus(ReservationStatus.ACTIVE);
            
            reservationRepository.save(reservation);
        }
    }
}
