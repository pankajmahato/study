package com.shivam151990.lld.movie_booking.model;

import lombok.Getter;

import java.util.List;
import java.util.UUID;


public class Screen {

    @Getter
    private UUID id;

    @Getter
    private String theatre;

    private final List<Seat> seats;

    public Screen(String theatre, List<Seat> seats) {
        this.theatre = theatre;
        this.seats = seats;
    }

    public void addSeats(Seat seat) {
        seats.add(seat);
    }
}
