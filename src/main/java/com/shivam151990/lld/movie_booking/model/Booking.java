package com.shivam151990.lld.movie_booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Booking {

    @Getter
    private UUID id;

    @NotNull
    @Getter
    private String userId;

    @NotNull
    @Getter
    private Show show;

    @NotNull
    @Getter
    private List<Seat> seats;

    @Getter
    private LocalDateTime bookingTime;

    private BookingStatus status;

    public Booking(String userId, Show show, List<Seat> seats) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.show = show;
        this.seats = seats;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.PENDING_PAYMENT;
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    public void confirmBooking() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancelBooking() {
        this.status = BookingStatus.FAILED;
    }
}
