package com.shivam151990.patterns.observer.general;

public class PhoneObserver implements NotificationObserver {

    private String ph;

    public PhoneObserver(String ph) {
        this.ph = ph;
    }

    @Override
    public String update() {
        return "Notification send to: " + ph;
    }
}
