package com.example.restaurantbooking.service;

import com.example.restaurantbooking.dto.PreferencesDto;
import com.example.restaurantbooking.entity.RestaurantTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoringService {
    
    private static final int SIZE_SCORE_WEIGHT = 40;
    private static final int PREFERENCE_SCORE_WEIGHT = 60;
    private static final double OPTIMAL_FILL_RATIO = 0.8; // 80% capacity
    

    public int calculateScore(RestaurantTable table, Integer partySize, PreferencesDto preferences) {
        int sizeScore = calculateSizeScore(table, partySize);
        int preferenceScore = calculatePreferenceScore(table, preferences);
        
        return (sizeScore * SIZE_SCORE_WEIGHT + preferenceScore * PREFERENCE_SCORE_WEIGHT)
            / (SIZE_SCORE_WEIGHT + PREFERENCE_SCORE_WEIGHT);
    }
    

    private int calculateSizeScore(RestaurantTable table, Integer partySize) {
        int capacity = table.getCapacity();
        
        if (capacity < partySize) {
            return 0; 
        }
        

        int optimalCapacity = (int) Math.ceil(partySize / OPTIMAL_FILL_RATIO);
        
        if (capacity == partySize) {
            return 100; 
        }
        
        if (capacity <= optimalCapacity) {
   
            int difference = capacity - partySize;
            int span = optimalCapacity - partySize;
            return 100 - (difference * 30 / Math.max(1, span));
        } else {
          
            int difference = capacity - optimalCapacity;
            return Math.max(20, 70 - (difference * 5));
        }
    }
    

    private int calculatePreferenceScore(RestaurantTable table, PreferencesDto preferences) {
        if (preferences == null) {
            return 50; 
        }
        
        int score = 50; 
        int maxBonus = 40;
        int bonusEarned = 0;
        
        if (preferences.getQuiet() != null && preferences.getQuiet() && 
            table.getQuiet() != null && table.getQuiet()) {
            bonusEarned += 10;
        }
        
        if (preferences.getNearWindow() != null && preferences.getNearWindow() && 
            table.getNearWindow() != null && table.getNearWindow()) {
            bonusEarned += 10;
        }
        
        if (preferences.getAccessible() != null && preferences.getAccessible() && 
            table.getAccessible() != null && table.getAccessible()) {
            bonusEarned += 10;
        }
        
        if (preferences.getNearKidsArea() != null && preferences.getNearKidsArea() && 
            table.getNearKidsArea() != null && table.getNearKidsArea()) {
            bonusEarned += 10;
        }
        
        score += Math.min(bonusEarned, maxBonus);
        
        return Math.min(100, score);
    }
    

    public String generateExplanation(RestaurantTable table, Integer partySize, PreferencesDto preferences) {
        StringBuilder reason = new StringBuilder();
        reason.append("Laud sobib ");
        reason.append(partySize).append("-liikmelisele seltskonnale");
        
        if (preferences != null) {
            boolean hasPreferences = false;
            if (Boolean.TRUE.equals(preferences.getQuiet()) && Boolean.TRUE.equals(table.getQuiet())) {
                reason.append(" ja asub vaikses kohas");
                hasPreferences = true;
            }
            if (Boolean.TRUE.equals(preferences.getNearWindow()) && Boolean.TRUE.equals(table.getNearWindow())) {
                reason.append(hasPreferences ? ", " : " ja ").append("akna lähedal");
                hasPreferences = true;
            }
            if (Boolean.TRUE.equals(preferences.getAccessible()) && Boolean.TRUE.equals(table.getAccessible())) {
                reason.append(hasPreferences ? ", " : " ja ").append("ligipääsetav");
                hasPreferences = true;
            }
            if (Boolean.TRUE.equals(preferences.getNearKidsArea()) && Boolean.TRUE.equals(table.getNearKidsArea())) {
                reason.append(hasPreferences ? ", " : " ja ").append("lasteala lähedal");
            }
        }
        reason.append(".");
        
        return reason.toString();
    }
}
