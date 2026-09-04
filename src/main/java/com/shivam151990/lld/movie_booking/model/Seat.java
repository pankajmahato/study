package com.shivam151990.lld.movie_booking.model;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Seat {
    private final UUID id;
    private final int row;
    private final char seatNumber;

    public Seat(int row, char seatNumber) {
        this.id = UUID.randomUUID();
        this.row = row;
        this.seatNumber = seatNumber;
    }

    @Override
    public String toString() {
        return row + "" + seatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return row == seat.row && seatNumber == seat.seatNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, seatNumber);
    }
}
