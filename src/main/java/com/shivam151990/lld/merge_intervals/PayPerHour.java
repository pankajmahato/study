package com.shivam151990.lld.merge_intervals;

import lombok.Getter;

import java.util.List;

public class PayPerHour implements PayStrategy {

    @Getter
    private final int ratePerHour;

    public PayPerHour(int ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public int pay(List<ShiftInterval> shifts) {
        int amount = 0;
        for (ShiftInterval shift: shifts) {
            amount += shift.getDuration() * ratePerHour;
        }
        return amount;
    }
}
