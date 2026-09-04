package com.shivam151990.lld.movie_booking.service;

import com.shivam151990.lld.movie_booking.model.Booking;
import com.shivam151990.lld.movie_booking.model.Seat;
import com.shivam151990.lld.movie_booking.model.Show;
import com.shivam151990.lld.movie_booking.repository.BookingRepository;

import java.util.List;

public class BookingService {

    private static final int MAX_RETRIES = 3;

    private final SeatLockService seatLockService;
    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;
    private final int maxRetries = MAX_RETRIES;

    public BookingService(SeatLockService seatLockService, BookingRepository bookingRepository, PaymentService paymentService) {
        this.seatLockService = seatLockService;
        this.bookingRepository = bookingRepository;
        this.paymentService = paymentService;
    }

    public Booking createBooking(String userId, Show show, List<Seat> seats) {
        for (Seat seat : seats) {
            if (bookingRepository.isSeatAlreadyBooked(show, seat)) {
                throw new IllegalStateException("Seat already booked: " + seat.getId());
            }
        }
        boolean isAvailable = seatLockService.lockSeats(show, userId, seats);
        if (!isAvailable) {
            throw new IllegalStateException("Some seats could not be locked");
        }

        Booking booking = new Booking(userId, show, seats);
        bookingRepository.save(booking);
        return booking;
    }

    public void cancelBooking(Booking booking) {
        seatLockService.unlockSeats(booking.getShow(), booking.getSeats());
        booking.cancelBooking();
    }

    public void confirmBooking(Booking booking, double amount) {
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries) {
            success = paymentService.processPayment(booking.getUserId(), amount, booking.getId());
            if (success) {
                break;
            }
            attempt++;
            System.out.println("Payment attempt " + attempt + " failed. Retrying...");
        }

        if (success) {
            seatLockService.markSeatsAsBooked(booking.getShow(), booking.getSeats());
            booking.confirmBooking();
            System.out.println("Booking confirmed: " + booking.getId());
        } else {
            seatLockService.unlockSeats(booking.getShow(), booking.getSeats());
            booking.cancelBooking();
            throw new RuntimeException("Payment failed after " + maxRetries + " attempts");
        }
    }
}
