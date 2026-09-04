package com.shivam151990.patterns.observer.item;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}
