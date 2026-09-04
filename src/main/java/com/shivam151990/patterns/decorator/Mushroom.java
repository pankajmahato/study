package com.shivam151990.patterns.decorator;

public class Mushroom implements Pizza {

    private Pizza pizza;

    public Mushroom(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public int cost() {
        return pizza.cost() + 30;
    }
}
