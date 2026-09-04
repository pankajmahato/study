package com.shivam151990.lld.parking_lot;

import com.shivam151990.lld.parking_lot.model.Vehicle;
import com.shivam151990.lld.parking_lot.model.VehicleType;
import com.shivam151990.lld.parking_lot.service.ParkingLot;
import com.shivam151990.lld.parking_lot.service.ParkingService;
import com.shivam151990.lld.parking_lot.strategy.ParkingStrategy;
import com.shivam151990.lld.parking_lot.strategy.first_free.FirstFreeStrategy;

public class ParkingLotRunner {

    public static void main(String[] args) {

        // Create Parking Lot
        ParkingLot parkingLot = ParkingLot.createParkingLotWithDefaultCapacity();

        // Create Parking Strategy and Parking Service
        ParkingStrategy parkingStrategy = new FirstFreeStrategy(parkingLot.getAllSpots());
        //
        ParkingService parkingService = new ParkingService(parkingStrategy);

        Vehicle v1 = new Vehicle("1", VehicleType.TWO_WHEELER);
        Vehicle v2 = new Vehicle("2", VehicleType.FOUR_WHEELER);
        Vehicle v3 = new Vehicle("3", VehicleType.TWO_WHEELER);

        // Park vehicles
        System.out.println(parkingService.parkVehicle(v1));
        System.out.println(parkingService.parkVehicle(v2));
        System.out.println(parkingService.parkVehicle(v3));

        // Get all tickets (we can get vehicles and their spots)
        System.out.println(parkingService.getAllTickets());

        // Get free spots for floor
        System.out.println("Two Wheeler: " + parkingService.getFreeSpotsForFloor(0, VehicleType.TWO_WHEELER));
        System.out.println("Four Wheeler: " + parkingService.getFreeSpotsForFloor(0, VehicleType.FOUR_WHEELER));
    }
}
