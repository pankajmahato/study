package com.shivam151990.patterns.template.tea_coffee;

public abstract class Beverage {

    public final void prepare() {
        boilWater();
        brew();
        pourInCup();
        addCondiments();
    }
    public void boilWater() {
        System.out.println("Boil Water");
    }
    public void pourInCup() {
        System.out.println("Pouring in cup");
    }

    public abstract void addCondiments();
    public abstract void brew();
}
