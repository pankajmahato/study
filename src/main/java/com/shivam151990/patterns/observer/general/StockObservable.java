package com.shivam151990.patterns.observer.general;

public interface StockObservable {

    void add(NotificationObserver notObs);
    void remove(NotificationObserver notObs);
    void notifySubscriber();
}

