package com.shivam151990.patterns.bridge.move;

public class Person extends Animal {

    public Person(Move move) {
        super(move);
    }

    @Override
    String getName() {
        return "person";
    }

    @Override
    void howToMove() {
        move.move();
    }
}
