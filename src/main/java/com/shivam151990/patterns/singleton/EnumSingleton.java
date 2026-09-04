package com.shivam151990.patterns.singleton;

public enum EnumSingleton {
    INSTANCE;

    private int count;

    public void increment() {
        System.out.println("Incremented counter");
        count++;
    }

    public int getCounter() {
        return count;
    }
}
