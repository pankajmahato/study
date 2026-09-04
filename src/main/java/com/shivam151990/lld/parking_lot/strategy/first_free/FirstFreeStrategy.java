package com.shivam151990.lld.parking_lot.strategy.first_free;

import com.shivam151990.lld.parking_lot.exception.ParkingSlotUnavailableException;
import com.shivam151990.lld.parking_lot.model.ParkingSpot;
import com.shivam151990.lld.parking_lot.model.ParkingTicket;
import com.shivam151990.lld.parking_lot.model.Vehicle;
import com.shivam151990.lld.parking_lot.model.VehicleType;
import com.shivam151990.lld.parking_lot.strategy.ParkingStrategy;

import java.util.*;

public class FirstFreeStrategy implements ParkingStrategy {

    private final PriorityQueue<ParkingSpot> pqTwoWheeler;
    private final PriorityQueue<ParkingSpot> pqFourWheeler;
    private final Map<ParkingTicket, ParkingSpot> ticketToSpotMap;

    public FirstFreeStrategy(List<ParkingSpot> spots) {
        this.ticketToSpotMap = new HashMap<>();
        this.pqTwoWheeler = new PriorityQueue<>(new FirstFreeComparator());
        this.pqFourWheeler = new PriorityQueue<>(new FirstFreeComparator());
        for (ParkingSpot spot: spots) {
            if (spot.getVehicleType() == VehicleType.TWO_WHEELER) {
                pqTwoWheeler.offer(spot);
            } else if (spot.getVehicleType() == VehicleType.FOUR_WHEELER) {
                pqFourWheeler.offer(spot);
            }
        }
    }

    @Override
    public ParkingTicket park(Vehicle vehicle) {
        ParkingSpot freeSpot = vehicle.getVehicleType() == VehicleType.TWO_WHEELER
                ? pqTwoWheeler.poll() : pqFourWheeler.poll();
        if (Objects.isNull(freeSpot)) {
            throw new ParkingSlotUnavailableException("No Parking available");
        }
        ParkingTicket ticket = new ParkingTicket(freeSpot.getSpotId(), vehicle.getVehicleNum());
        ticketToSpotMap.put(ticket, freeSpot);
        return ticket;
    }

    @Override
    public void unPark(ParkingTicket parkingTicket) {
        ParkingSpot spot = ticketToSpotMap.remove(parkingTicket);
        if (spot.getVehicleType() == VehicleType.TWO_WHEELER) {
            pqTwoWheeler.offer(spot);
        } else if (spot.getVehicleType() == VehicleType.FOUR_WHEELER) {
            pqFourWheeler.offer(spot);
        }
    }

    @Override
    public List<ParkingSpot> getFreeSpots(VehicleType type) {
        if (type == VehicleType.TWO_WHEELER) {
            return pqTwoWheeler.stream().toList();
        } else {
            return pqFourWheeler.stream().toList();
        }
    }
}
