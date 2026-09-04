package com.shivam151990;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Solution {

    public static void main(String[] args) {
        LocalDateTime t1 = LocalDateTime.of(2025, 8, 22, 10, 0);
        LocalDateTime t2 = LocalDateTime.now();

        // 1st way
        long between = ChronoUnit.MINUTES.between(t1, t2);
        System.out.println("ChronUnit Minutes: " + between);

        // 2nd way
        Duration between1 = Duration.between(t1, t2);
        System.out.println("Duration Minutes: " + between1.getSeconds() / 60);
    }
}