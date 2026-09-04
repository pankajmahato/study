package com.shivam151990.patterns.observer.general;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PhoneStockObservable implements StockObservable {

    @Getter
    private int phoneCount;

    private List<NotificationObserver> observerList;

    public PhoneStockObservable() {
        observerList = new ArrayList<>();
    }

    @Override
    public void add(NotificationObserver notObs) {
        observerList.add(notObs);
    }

    @Override
    public void remove(NotificationObserver notObs) {
        observerList.remove(notObs);
    }

    @Override
    public void notifySubscriber() {
        if (phoneCount > 0) {
            for (NotificationObserver obs : observerList) {
                System.out.println(obs.update());
            }
        }
    }

    public void setPhoneCount(int phoneCount) {
        this.phoneCount = phoneCount;
        notifySubscriber();
    }
}
