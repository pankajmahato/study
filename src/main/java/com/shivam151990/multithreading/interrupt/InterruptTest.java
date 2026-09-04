package com.shivam151990.multithreading.interrupt;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
/*

1. Interrupting a Thread in sleep():
    -> When a thread is in a blocking state (e.g., sleep(), wait(), join(), etc.),
        calling interrupt() on it will:
    -> Wake the thread up from the blocking state.
    -> Throw an InterruptedException in the thread.
    -> Clear the interrupt status (i.e., set it to false).
2. Interrupting a Thread Not in sleep():
    -> When a thread is not in a blocking state, calling interrupt() on it will:
    -> Set the interrupt status of the thread to true.
    -> No exception is thrown. The thread must explicitly check its interrupt status
        (e.g., using Thread.interrupted() or Thread.isInterrupted()) to respond to the interruption.
 */



public class InterruptTest {

    @SneakyThrows
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
                System.out.println("DOING TASK");
                for (int i = 1; i <= 1000; i++) {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    System.out.println("Task: " + i);
                }
            } catch (InterruptedException ex) {
                /*
                    After catching the interrupted Exception the interrupt status is cleared (set to false).
                 */
                System.out.println("Interrupt status : " + Thread.currentThread().isInterrupted());
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("Will interrupt in 5 sec...");
                TimeUnit.MILLISECONDS.sleep(5000);
                t1.interrupt();
            } catch (InterruptedException ex) {
                System.out.println("Interrupt status : " + Thread.currentThread().isInterrupted());
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
