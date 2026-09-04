package com.shivam151990.lld.parking_lot.model;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSpot {

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    @Getter
    private final int spotId;

    @Getter
    private final int floor;

    @Getter
    private final int row;

    @Getter
    private final int col;

    @Getter
    private final VehicleType vehicleType;

    public ParkingSpot(int floor, int row, int col, VehicleType vehicleType) {
        this.spotId = idCounter.getAndIncrement();
        this.floor = floor;
        this.row = row;
        this.col = col;
        this.vehicleType = vehicleType;
    }
}
