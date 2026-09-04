package com.shivam151990.multithreading.zpractice;

import java.util.Random;

public class Runner {

    public static void main(String[] args) throws InterruptedException {
        Random r1 = new Random(100);

        BlockingQueueWithLock<Integer> qu = new BlockingQueueWithLock<>(10);

        Thread t1 = new Thread(() -> {
            while (true) {
                sleep(500);
                qu.offer(r1.nextInt());
            }
        });

        Thread t2 = new Thread(() -> {
           while (true) {
               sleep(1000);
               qu.poll();
           }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    public static void sleep(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
