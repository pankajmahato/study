package com.shivam151990.lld.tennis_club_booking.strategy;

import com.shivam151990.lld.tennis_club_booking.BookingRecord;
import com.shivam151990.lld.tennis_club_booking.Court;

import java.util.List;
import java.util.Map;

public interface CourtAssignmentStrategy {
    Map<BookingRecord, Court> assignCourts(List<BookingRecord> bookings);
}
