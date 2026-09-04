package com.shivam151990.lld.parking_lot.service;

import com.shivam151990.lld.parking_lot.model.ParkingSpot;
import com.shivam151990.lld.parking_lot.model.VehicleType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will act as Facade to expose parking lot features
 */
public class ParkingLot {

    private int totalCapacity;
    private final int floors;
    private final int rowPerFloor;
    private final int colPerFloor;

    private static final int DEFAULT_FLOORS = 3;
    private static final int DEFAULT_ROWS = 5;
    private static final int DEFAULT_COLS = 10;

    @Getter
    private final List<ParkingSpot> allSpots;

    public static ParkingLot createParkingLotWithDefaultCapacity() {
        return new ParkingLot(DEFAULT_FLOORS, DEFAULT_ROWS, DEFAULT_COLS);
    }

    public static ParkingLot createParkingLotWithGivenCapacity(int floors, int rowPerFloor, int colPerFloor) {
        return new ParkingLot(floors, rowPerFloor, colPerFloor);
    }

    private ParkingLot(int floors, int rowPerFloor, int colPerFloor) {
        this.allSpots = new ArrayList<>();
        this.floors = floors;
        this.rowPerFloor = rowPerFloor;
        this.colPerFloor = colPerFloor;
        createParkingSpots();
    }

    /**
     * Parking spots will be created per floor
     * and parking slots will be equal per category.
     */
    public void createParkingSpots() {
        for (int floor = 0; floor < floors; floor++) {
            for (int r = 0; r < rowPerFloor; r++) {
                for (int c = 0; c < colPerFloor; c++){
                    if (r % 2 == 0) {
                        allSpots.add(new ParkingSpot(floor, r, c, VehicleType.TWO_WHEELER));
                    } else {
                        allSpots.add(new ParkingSpot(floor, r, c, VehicleType.FOUR_WHEELER));
                    }
                }
            }
        }
    }
}
