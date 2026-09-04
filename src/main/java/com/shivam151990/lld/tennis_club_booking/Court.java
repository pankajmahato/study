package com.shivam151990.lld.tennis_club_booking;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a court and its assigned bookings.
 */
public class Court {

    int id;

    @Getter
    @Setter
    int usageCount = 0;

    @Getter
    @Setter
    int nextAvailableTime = 0;

    public Court(int id) {
        this.id = id;
    }

    public void incrementUsageCount() {
        usageCount++;
    }

    @Override
    public String toString() {
        return "Court#" + id;
    }
}

