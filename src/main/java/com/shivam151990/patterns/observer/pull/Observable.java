package com.shivam151990.patterns.observer.pull;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
    String getState();
}
