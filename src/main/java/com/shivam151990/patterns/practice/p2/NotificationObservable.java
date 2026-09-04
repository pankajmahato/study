package com.shivam151990.patterns.practice.p2;

public interface NotificationObservable {

    void add(NotificationObserver obs);

    void remove(NotificationObserver obs);

    void notifyMessage();
}
