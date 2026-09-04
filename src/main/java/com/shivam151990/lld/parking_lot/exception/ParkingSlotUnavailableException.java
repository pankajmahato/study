package com.shivam151990.lld.parking_lot.exception;

public class ParkingSlotUnavailableException extends RuntimeException {

    public ParkingSlotUnavailableException(String message) {
        super(message);
    }

    public ParkingSlotUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParkingSlotUnavailableException(Throwable cause) {
        super(cause);
    }
}
