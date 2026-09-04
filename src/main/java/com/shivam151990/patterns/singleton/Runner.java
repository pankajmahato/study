package com.shivam151990.patterns.singleton;

public class Runner {
    public static void main(String[] args) {
        EnumSingleton singleton = EnumSingleton.INSTANCE;
        singleton.increment();
        System.out.println(singleton.getCounter());
    }
}
