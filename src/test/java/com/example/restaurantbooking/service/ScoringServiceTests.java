package com.example.restaurantbooking.service;

import com.example.restaurantbooking.dto.PreferencesDto;
import com.example.restaurantbooking.entity.RestaurantTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoringServiceTests {
    
    private final ScoringService scoringService = new ScoringService();
    
    @Test
    public void testPerfectSizeMatch() {
        RestaurantTable table = new RestaurantTable();
        table.setCapacity(4);
        table.setQuiet(false);
        table.setNearWindow(false);
        table.setAccessible(false);
        table.setNearKidsArea(false);
        
        int score = scoringService.calculateScore(table, 4, new PreferencesDto());
        assertTrue(score >= 40); // Should have good base score
    }
    
    @Test
    public void testOversizedTable() {
        RestaurantTable table = new RestaurantTable();
        table.setCapacity(8);
        table.setQuiet(false);
        table.setNearWindow(false);
        table.setAccessible(false);
        table.setNearKidsArea(false);
        
        int score = scoringService.calculateScore(table, 2, new PreferencesDto());
        assertTrue(score > 0);
    }
    
    @Test
    public void testUndersizedTable() {
        RestaurantTable table = new RestaurantTable();
        table.setCapacity(2);
        table.setQuiet(false);
        table.setNearWindow(false);
        table.setAccessible(false);
        table.setNearKidsArea(false);
        
        int score = scoringService.calculateScore(table, 4, new PreferencesDto());
        assertEquals(0, score); // Table too small
    }
    
    @Test
    public void testPreferencesBonus() {
        RestaurantTable table = new RestaurantTable();
        table.setCapacity(4);
        table.setQuiet(true);
        table.setNearWindow(true);
        table.setAccessible(false);
        table.setNearKidsArea(false);
        
        PreferencesDto prefs = new PreferencesDto();
        prefs.setQuiet(true);
        prefs.setNearWindow(true);
        
        int score = scoringService.calculateScore(table, 4, prefs);
        assertTrue(score > 50); // Should have preference bonus
    }

    @Test
    public void testExplanationIncludesKidsAreaPreference() {
        RestaurantTable table = new RestaurantTable();
        table.setCapacity(4);
        table.setNearKidsArea(true);

        PreferencesDto prefs = new PreferencesDto();
        prefs.setNearKidsArea(true);

        String explanation = scoringService.generateExplanation(table, 4, prefs);
        assertTrue(explanation.contains("lasteala"));
    }
}
