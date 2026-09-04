package com.shivam151990.lld.tennis_club_booking;

import lombok.Getter;

/**
 * Represents a booking for a tennis court.
 */
public class BookingRecord {
    int id;

    @Getter
    int startTime;

    @Getter
    int finishTime;

    public BookingRecord(int id, int startTime, int finishTime) {
        this.id = id;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
