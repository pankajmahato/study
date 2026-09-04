package com.shivam151990.patterns.observer.push;

import java.util.ArrayList;
import java.util.List;

public class ObservableSubject implements Observable {

    private List<Observer> observers;

    public ObservableSubject() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer ob: observers) {
            ob.update("New Update happened!!!"); // Push the update here
        }
    }
}
