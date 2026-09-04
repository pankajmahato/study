package com.shivam151990.patterns.observer.pull;

public class ConcreteObserver implements Observer {
    @Override
    public void update(Observable subject) {
        System.out.println("Received subject: " + subject.getState());
    }
}
