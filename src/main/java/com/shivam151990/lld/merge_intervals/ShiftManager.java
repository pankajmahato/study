package com.shivam151990.lld.merge_intervals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShiftManager {

    private List<ShiftInterval> shifts;

    public ShiftManager() {
        shifts = new ArrayList<>();
    }

    public void addShifts(ShiftInterval shift) {
        shifts.add(shift);
    }

    public List<ShiftInterval> getMergedShifts() {
        if (shifts.isEmpty()) {
            System.out.println("No Shifts to merge!!!");
            return Collections.emptyList();
        }
        shifts.sort(Comparator.comparingLong(ShiftInterval::getStart));

        List<ShiftInterval> mergedShifts = new ArrayList<>();
        ShiftInterval current = shifts.getFirst();

        for (int i = 1; i < shifts.size(); i++) {
            ShiftInterval next = shifts.get(i);

            if (next.getStart() <= current.getEnd()) {
                // Overlapping intervals, merge them
                current = new ShiftInterval(
                        Math.min(current.getStart(), next.getStart()),
                        Math.max(current.getEnd(), next.getEnd())
                );
            } else {
                // Non-overlapping interval, add current to merged list
                mergedShifts.add(current);
                current = next;
            }
        }
        mergedShifts.add(current);
        return mergedShifts;
    }

    public int calculatePay(PayStrategy payStrategy, List<ShiftInterval> mergedShifts) {
        return payStrategy.pay(mergedShifts);
    }
}
