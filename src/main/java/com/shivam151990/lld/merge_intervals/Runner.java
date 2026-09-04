package com.shivam151990.lld.merge_intervals;

import java.util.List;

public class Runner {
    public static void main(String[] args) {
        ShiftManager sm = new ShiftManager();
        ShiftInterval s1 = new ShiftInterval(1, 2);
        ShiftInterval s2 = new ShiftInterval(2, 5);

        sm.addShifts(s1);
        sm.addShifts(s2);
        List<ShiftInterval> mergedShifts = sm.getMergedShifts();

        PayStrategy payStrategy = new PayPerHour(10);
        PayStrategy payWithBonus = new BonusPayDecorator(payStrategy, 5);

        System.out.println(sm.calculatePay(payWithBonus, mergedShifts));

    }
}
