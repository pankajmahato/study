package com.shivam151990.lld.movie_booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Show {

    @Getter
    private UUID id;

    @Getter
    private Movie movie;

    @Getter
    private LocalDateTime startTime;

    @Getter
    private Duration duration;

    @Getter
    private Screen screen;

    public Show(@NotNull String id,
                @NotNull Movie movie,
                @NotNull Screen screen,
                @NotNull LocalDateTime startTime,
                @NotNull Duration duration) {
        this.id = UUID.randomUUID();
        this.movie = movie;
        this.startTime = startTime;
        this.duration = duration;
        this.screen = screen;
    }
}
