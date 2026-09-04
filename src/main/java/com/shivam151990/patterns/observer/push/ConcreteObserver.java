package com.shivam151990.patterns.observer.push;

public class ConcreteObserver implements Observer {
    @Override
    public void update(String update) {
        System.out.println("Message Received: " + update);
    }
}
