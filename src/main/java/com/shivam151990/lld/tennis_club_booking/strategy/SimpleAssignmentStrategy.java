package com.shivam151990.lld.tennis_club_booking.strategy;

import com.shivam151990.lld.tennis_club_booking.BookingRecord;
import com.shivam151990.lld.tennis_club_booking.Court;

import java.util.*;

public class SimpleAssignmentStrategy implements CourtAssignmentStrategy {
    @Override
    public Map<BookingRecord, Court> assignCourts(List<BookingRecord> bookings) {
        bookings.sort(Comparator.comparingInt(BookingRecord::getStartTime));

        PriorityQueue<Court> availableCourts = new PriorityQueue<>(Comparator.comparingInt(Court::getNextAvailableTime));
        Map<BookingRecord, Court> assignment = new HashMap<>();
        int courtCounter = 1;

        for (BookingRecord booking : bookings) {
            if (!availableCourts.isEmpty() && availableCourts.peek().getNextAvailableTime() <= booking.getStartTime()) {
                Court court = availableCourts.poll();
                court.setNextAvailableTime(booking.getFinishTime());
                assignment.put(booking, court);
                availableCourts.offer(court);
            } else {
                Court newCourt = new Court(courtCounter++);
                newCourt.setNextAvailableTime(booking.getFinishTime());
                assignment.put(booking, newCourt);
                availableCourts.offer(newCourt);
            }
        }
        return assignment;
    }
}
