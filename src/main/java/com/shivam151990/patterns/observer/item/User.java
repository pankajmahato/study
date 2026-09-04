package com.shivam151990.patterns.observer.item;

public class User implements Observer {

    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(int update) {
        System.out.println(name + " Received Quantity updated for Item");
    }
}
