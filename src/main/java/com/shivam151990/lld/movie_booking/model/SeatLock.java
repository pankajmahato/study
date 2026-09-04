package com.shivam151990.lld.movie_booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

public class SeatLock {

    private static final Long DEFAULT_TIMEOUT = 60L;

    @NotNull
    @Getter
    private Seat seat;

    @NotNull
    @Getter
    private Show show;

    @NotNull
    @Getter
    private String userId;

    @NotNull
    @Getter
    private final LocalDateTime lockTime;

    private Long timeoutInSec;

    public SeatLock(Seat seat, Show show, String userId, LocalDateTime lockTime) {
        this.seat = seat;
        this.show = show;
        this.userId = userId;
        this.lockTime = lockTime;
        this.timeoutInSec = DEFAULT_TIMEOUT;
    }

    public boolean isExpired() {
        return Duration.between(lockTime, LocalDateTime.now()).getSeconds() > timeoutInSec;
    }
}
