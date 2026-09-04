package com.shivam151990.lld.tennis_club_booking;

import com.shivam151990.lld.tennis_club_booking.strategy.CourtAssignmentStrategy;

import java.util.List;
import java.util.Map;

public class TennisCourtScheduler {
    private final CourtAssignmentStrategy strategy;

    public TennisCourtScheduler(CourtAssignmentStrategy strategy) {
        this.strategy = strategy;
    }

    public Map<BookingRecord, Court> schedule(List<BookingRecord> bookings) {
        return strategy.assignCourts(bookings);
    }
}
