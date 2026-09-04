package com.shivam151990.lld.movie_booking.repository;

import com.shivam151990.lld.movie_booking.model.Booking;
import com.shivam151990.lld.movie_booking.model.Seat;
import com.shivam151990.lld.movie_booking.model.Show;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();

    @Override
    public void save(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public List<Booking> getBookingsByShow(Show show) {
        return bookings.stream()
                .filter(b -> b.getShow().equals(show))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSeatAlreadyBooked(Show show, Seat seat) {
        return bookings.stream()
                .filter(Booking::isConfirmed)
                .filter(b -> b.getShow().equals(show))
                .flatMap(b -> b.getSeats().stream())
                .anyMatch(s -> s.equals(seat));
    }
}
