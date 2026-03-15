package com.example.restaurantbooking.entity;

public enum ReservationStatus {
    ACTIVE("Aktiivne"),
    CANCELLED("Tühistatud"),
    COMPLETED("Lõpetatud");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
