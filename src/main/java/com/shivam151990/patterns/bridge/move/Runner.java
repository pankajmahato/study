package com.shivam151990.patterns.bridge.move;

public class Runner {
    public static void main(String[] args) {
        Animal person = new Person(new Walk());
        person.howToMove();

        Animal fish = new Fish(new Swim());
        fish.howToMove();
    }
}
