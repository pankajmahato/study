package com.shivam151990.lld.movie_booking.repository;

import com.shivam151990.lld.movie_booking.model.Booking;
import com.shivam151990.lld.movie_booking.model.Seat;
import com.shivam151990.lld.movie_booking.model.Show;

import java.util.List;

public interface BookingRepository {
    void save(Booking booking);
    List<Booking> getBookingsByShow(Show show);
    boolean isSeatAlreadyBooked(Show show, Seat seat);
}
