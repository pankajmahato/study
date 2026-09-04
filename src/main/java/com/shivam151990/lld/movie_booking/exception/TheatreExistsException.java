package com.shivam151990.lld.movie_booking.exception;

public class TheatreExistsException extends RuntimeException {

    public TheatreExistsException() {
        super();
    }

    public TheatreExistsException(String message) {
        super(message);
    }

    public TheatreExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
