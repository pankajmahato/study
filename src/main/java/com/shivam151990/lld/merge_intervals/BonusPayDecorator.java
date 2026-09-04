package com.shivam151990.lld.merge_intervals;

import java.util.List;

public class BonusPayDecorator implements PayStrategy {

    private PayStrategy payStrategy;
    private int bonus;

    public BonusPayDecorator(PayStrategy payStrategy, int bonus) {
        this.payStrategy = payStrategy;
        this.bonus = bonus;
    }

    @Override
    public int pay(List<ShiftInterval> shifts) {
        return  payStrategy.pay(shifts) + bonus;
    }
}
