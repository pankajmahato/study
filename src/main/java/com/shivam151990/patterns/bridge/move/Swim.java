package com.shivam151990.patterns.bridge.move;

public class Swim implements Move {
    @Override
    public void move() {
        System.out.println("Swimming");
    }
}
