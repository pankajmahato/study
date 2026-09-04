package com.shivam151990.lld.parking_lot.service;

import com.shivam151990.lld.parking_lot.exception.VehicleNotFoundException;
import com.shivam151990.lld.parking_lot.model.ParkingSpot;
import com.shivam151990.lld.parking_lot.model.ParkingTicket;
import com.shivam151990.lld.parking_lot.model.Vehicle;
import com.shivam151990.lld.parking_lot.model.VehicleType;
import com.shivam151990.lld.parking_lot.strategy.ParkingStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingService {

    private ParkingStrategy parkingStrategy;
    private Map<Vehicle, ParkingTicket> tickets;

    public ParkingService(ParkingStrategy parkingStrategy) {
        this.tickets = new HashMap<>();
        this.parkingStrategy = parkingStrategy;
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        ParkingTicket ticket = parkingStrategy.park(vehicle);
        tickets.put(vehicle, ticket);
        return ticket;
    }

    public void unPark(Vehicle vehicle) {
        if (!tickets.containsKey(vehicle)) {
            throw new VehicleNotFoundException("Vehicle not found in Parking Lot");
        }
        ParkingTicket parkingTicket = tickets.remove(vehicle);
        parkingStrategy.unPark(parkingTicket);
    }

    public Vehicle search(String vehicleNum) {
        return tickets.keySet().stream()
                .filter(ticket -> ticket.getVehicleNum().equals(vehicleNum))
                .findFirst()
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found in parking lot"));
    }

    public int getFreeSpotsForFloor(int floor, VehicleType vehicleType) {
        List<ParkingSpot> spots = parkingStrategy.getFreeSpots(vehicleType);
        long count = spots.stream()
                .filter(s -> s.getFloor() == floor)
                .count();
        return (int) count;
    }

    public Collection<ParkingTicket> getAllTickets() {
        return tickets.values();
    }
}
