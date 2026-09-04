package com.shivam151990.lld.parking_lot.model;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingTicket {

    private static AtomicInteger id = new AtomicInteger(0);

    @Getter
    private int ticketNum;

    @Getter
    private int spotId;

    @Getter
    private String vehicleNum;

    public ParkingTicket(int spotId, String vehicleNum) {
        this.ticketNum = id.getAndIncrement();
        this.spotId = spotId;
        this.vehicleNum = vehicleNum;
    }

    @Override
    public String toString() {
        return "ParkingTicket { " +
                "ticketNum=" + ticketNum +
                ", spotId=" + spotId +
                ", vehicleNum='" + vehicleNum + '\'' +
                " } ";
    }
}
