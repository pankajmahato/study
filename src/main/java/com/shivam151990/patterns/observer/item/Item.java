package com.shivam151990.patterns.observer.item;

import java.util.ArrayList;
import java.util.List;

public class Item implements Observable {

    private List<Observer> observers;
    int item;

    public Item() {
        observers = new ArrayList<>();
        item = 0;
    }

    public void updateItemQuantity(int count) {
        item += count;
        for (Observer ob: observers) {
            ob.update(item);
        }
    }

    @Override
    public void addObserver(Observer observer) {
    observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
}
