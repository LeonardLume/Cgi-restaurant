package com.example.restaurantbooking.entity;

public enum Zone {
    TERRACE("Terrass"),
    MAIN_HALL("Peamine saal"),
    PRIVATE_ROOM("Privaatne ruum");

    private final String displayName;

    Zone(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
