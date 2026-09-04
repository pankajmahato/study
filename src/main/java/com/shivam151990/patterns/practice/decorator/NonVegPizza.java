package com.shivam151990.patterns.practice.decorator;

public class NonVegPizza implements Pizza {

    @Override
    public double cost() {
        return 120.0;
    }
}
