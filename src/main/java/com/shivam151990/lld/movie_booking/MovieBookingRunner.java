package com.shivam151990.lld.movie_booking;

import com.shivam151990.lld.movie_booking.model.*;
import com.shivam151990.lld.movie_booking.repository.InMemoryBookingRepository;
import com.shivam151990.lld.movie_booking.service.BookingService;
import com.shivam151990.lld.movie_booking.service.DummyPaymentService;
import com.shivam151990.lld.movie_booking.service.SeatLockService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieBookingRunner {

    public static void main(String[] args) {
        // Create Seats
        List<Seat> seats = new ArrayList<>();
        for (int r = 1; r <= 5; r++) {
            for (char c = 'A'; c <= 'K'; c++) {
                seats.add(new Seat(r, c));
            }
        }

        // Create Screen
        Screen screen = new Screen("S1", seats);

        // Create Show
        Show show = new Show("show_1", new Movie("M1","Inception"),
                screen, LocalDateTime.now(), Duration.ofHours(2));

        // Book Seats
        Seat s1 = new Seat(1, 'A');
        Seat s2 = new Seat(1, 'B');

        BookingService bookingService = new BookingService(
            new SeatLockService(),
            new InMemoryBookingRepository(),
            new DummyPaymentService()
        );

        // Create Booking
        Booking user1Booking = bookingService.createBooking("user_1", show, List.of(s1, s2));
        bookingService.confirmBooking(user1Booking, 100);

    }
}
