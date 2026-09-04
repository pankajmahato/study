package com.shivam151990.lld.tennis_club_booking.strategy;

import com.shivam151990.lld.tennis_club_booking.BookingRecord;
import com.shivam151990.lld.tennis_club_booking.Court;

import java.util.*;

public class DurabilityAssignmentStrategy implements CourtAssignmentStrategy {
    private final int maintenanceTime;
    private final int durabilityLimit;

    public DurabilityAssignmentStrategy(int maintenanceTime, int durabilityLimit) {
        this.maintenanceTime = maintenanceTime;
        this.durabilityLimit = durabilityLimit;
    }

    @Override
    public Map<BookingRecord, Court> assignCourts(List<BookingRecord> bookings) {
        bookings.sort(Comparator.comparingInt(BookingRecord::getStartTime));

        PriorityQueue<Court> availableCourts = new PriorityQueue<>(Comparator.comparingInt(Court::getNextAvailableTime));
        Map<BookingRecord, Court> assignment = new HashMap<>();
        int courtCounter = 1;

        for (BookingRecord booking : bookings) {
            if (!availableCourts.isEmpty() && availableCourts.peek().getNextAvailableTime() <= booking.getStartTime()) {
                Court court = availableCourts.poll();
                court.incrementUsageCount();
                if (court.getUsageCount() >= durabilityLimit) {
                    court.setNextAvailableTime(booking.getFinishTime() + maintenanceTime);
                    court.setUsageCount(0); // reset after maintenance
                } else {
                    court.setNextAvailableTime(booking.getFinishTime());
                }
                assignment.put(booking, court);
                availableCourts.offer(court);
            } else {
                Court newCourt = new Court(courtCounter++);
                newCourt.setNextAvailableTime(booking.getFinishTime());
                newCourt.setUsageCount(1);
                assignment.put(booking, newCourt);
                availableCourts.offer(newCourt);
            }
        }
        return assignment;
    }
}
