package com.shivam151990.patterns.observer.item;

public class Runner {
    public static void main(String[] args) {
        Item i1 = new Item();
        User u1 = new User("Shivam");
        User u2 = new User("Ashish");

        i1.addObserver(u1);
        i1.addObserver(u2);

        i1.updateItemQuantity(10);


    }
}
