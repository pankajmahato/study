package com.shivam151990.patterns.observer.pull;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ObservableSubject implements Observable {
    private List<Observer> observers;

    @Getter
    private String state;

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

    public void setState(String newState) {
        this.state = newState;
        notifyObservers();
    }

    public void notifyObservers() {
        for (Observer obs : observers) {
            obs.update(this);  // Just notify, no data pushed
        }
    }
}
