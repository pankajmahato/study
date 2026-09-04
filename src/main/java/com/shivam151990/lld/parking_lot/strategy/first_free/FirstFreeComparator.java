package com.shivam151990.lld.parking_lot.strategy.first_free;

import com.shivam151990.lld.parking_lot.model.ParkingSpot;

import java.util.Comparator;

public class FirstFreeComparator implements Comparator<ParkingSpot> {

    @Override
    public int compare(ParkingSpot o1, ParkingSpot o2) {
        if (o1.getFloor() != o2.getFloor()) {
            return Integer.compare(o1.getFloor(), o2.getFloor());
        }
        if (o1.getRow() != o2.getRow()) {
            return Integer.compare(o1.getRow(), o2.getRow());
        }
        return Integer.compare(o1.getCol(), o2.getCol());
    }
}
