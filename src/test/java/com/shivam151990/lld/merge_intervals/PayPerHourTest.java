package com.shivam151990.lld.merge_intervals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class PayPerHourTest {

    private PayPerHour payPerHour;

    @BeforeEach
    void setUp() {
        payPerHour = new PayPerHour(10);
    }

    @Test
    void paySuccess() {
        List<ShiftInterval> intervals = List.of(
                new ShiftInterval(1, 2),
                new ShiftInterval(2, 5)
        );
        int val = payPerHour.pay(intervals);
        Assertions.assertEquals(40, val, "Incorrect pay calculation");
    }

    @Test
    void getRatePerHour() {
        Assertions.assertEquals(10, payPerHour.getRatePerHour());
    }
}