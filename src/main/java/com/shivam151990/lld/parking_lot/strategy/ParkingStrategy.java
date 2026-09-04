package com.shivam151990.lld.parking_lot.strategy;

import com.shivam151990.lld.parking_lot.model.ParkingSpot;
import com.shivam151990.lld.parking_lot.model.ParkingTicket;
import com.shivam151990.lld.parking_lot.model.Vehicle;
import com.shivam151990.lld.parking_lot.model.VehicleType;

import java.util.List;

public interface ParkingStrategy {
    ParkingTicket park(Vehicle vehicle);
    void unPark(ParkingTicket parkingTicket);
    List<ParkingSpot> getFreeSpots(VehicleType type);
}
