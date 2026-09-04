package com.shivam151990.patterns.decorator;

public class ExtraCheese implements Topping {

    private final Pizza pizza;

    public ExtraCheese(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public int cost() {
        return pizza.cost() + 20;
    }
}
