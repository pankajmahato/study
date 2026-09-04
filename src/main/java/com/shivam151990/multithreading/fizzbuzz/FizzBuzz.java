package com.shivam151990.multithreading.fizzbuzz;

public class FizzBuzz {

    private int count;
    private final int maxCount;

    public FizzBuzz(int maxCount) {
        this.count = 1;
        this.maxCount = maxCount;
    }

    public synchronized void number() throws InterruptedException {
        while (count <= maxCount) {
            while (count <= maxCount && count % 3 == 0 || count % 5 == 0) {
                wait();
            }
            System.out.println("Number: " + count);
            count++;
            notifyAll();
        }
    }

    public synchronized void fizz() throws InterruptedException {
        while (count <= maxCount) {
            while (count <= maxCount && !(count % 3 == 0 && count % 5 != 0)) {
                wait();
            }
            System.out.println("Fizz");
            count++;
            notifyAll();
        }
    }

    public synchronized void buzz() throws InterruptedException {
        while (count <= maxCount) {
            while (count <= maxCount && !(count % 5 == 0 && count % 3 != 0)) {
                wait();
            }
            System.out.println("Buzz");
            count++;
            notifyAll();
        }
    }

    public synchronized void fizzBuzz() throws InterruptedException {
        while (count <= maxCount) {
            while (count <= maxCount && !(count % 3 == 0 && count % 5 == 0)) {
                wait();
            }
            System.out.println("FizzBuzz");
            count++;
            notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        FizzBuzz fb = new FizzBuzz(21);
        Thread t1 = new Thread(() -> {
            try {
                fb.number();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                fb.fizz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t3 = new Thread(() -> {
            try {
                fb.buzz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t4 = new Thread(() -> {
            try {
                fb.fizzBuzz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

//        t1.join();
//        t2.join();
//        t3.join();
//        t4.join();
    }
}
