package com.shivam151990.patterns.bridge.move;

public class Walk implements Move {

    @Override
    public void move() {
        System.out.println("Walking");
    }
}
