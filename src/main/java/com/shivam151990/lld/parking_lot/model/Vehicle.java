package com.shivam151990.lld.parking_lot.model;

import lombok.Getter;

public class Vehicle {
    @Getter
    private String vehicleNum;

    @Getter
    private VehicleType vehicleType;

    public Vehicle(String vehicleNum, VehicleType vehicleType) {
        this.vehicleNum = vehicleNum;
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleNum='" + vehicleNum + '\'' +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
