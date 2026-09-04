package com.shivam151990.multithreading.sandwich;

import java.util.concurrent.Semaphore;

public class Sandwich {

    private int count;
    private final int max;
    private final Semaphore zeroSem;
    private final Semaphore numSem;

    public Sandwich(int max) {
        this.max = max;
        this.count = 0;
        this.numSem = new Semaphore(1);
        this.zeroSem = new Semaphore(0);
    }

    public void printZero() throws InterruptedException {
        while (count <= max) {
            zeroSem.acquire();
            System.out.println("Zero: " + 0);
            numSem.release();
        }
    }

    public void printNumber() throws InterruptedException {
        while (count <= max) {
            numSem.acquire();
            System.out.println("Number: " + count);
            count++;
            zeroSem.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Sandwich sw = new Sandwich(20);
        Thread t1 = new Thread(() -> {
            try {
                sw.printNumber();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                sw.printZero();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();

//        t1.join();
//        t2.join();
    }
}
