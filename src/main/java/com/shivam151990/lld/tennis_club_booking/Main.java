package com.shivam151990.lld.tennis_club_booking;

import com.shivam151990.lld.tennis_club_booking.strategy.DurabilityAssignmentStrategy;
import com.shivam151990.lld.tennis_club_booking.strategy.MaintenanceAssignmentStrategy;
import com.shivam151990.lld.tennis_club_booking.strategy.SimpleAssignmentStrategy;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<BookingRecord> bookings = Arrays.asList(
                new BookingRecord(1, 1, 4),
                new BookingRecord(2, 2, 5),
                new BookingRecord(3, 6, 8),
                new BookingRecord(4, 7, 9)
        );

        TennisCourtScheduler scheduler1 = new TennisCourtScheduler(new SimpleAssignmentStrategy());
        System.out.println("Simple Strategy: " + scheduler1.schedule(bookings));

        TennisCourtScheduler scheduler2 = new TennisCourtScheduler(new MaintenanceAssignmentStrategy(2));
        System.out.println("With Maintenance: " + scheduler2.schedule(bookings));

        TennisCourtScheduler scheduler3 = new TennisCourtScheduler(new DurabilityAssignmentStrategy(3, 2));
        System.out.println("With Durability: " + scheduler3.schedule(bookings));
    }
}
