package com.shivam151990.multithreading.zpractice;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

    private T element;
    private final int maxCapacity;
    private Queue<T> qu;

    public BlockingQueue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.qu = new LinkedList<>();
    }

    public synchronized void offer(T ele) {
        try {
            while(qu.size() == maxCapacity) {
                System.out.println("Queue Full!!");
                wait();
            }
            System.out.println("Added element: " + ele);
            qu.offer(ele);
            notifyAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized T poll() {
        T element = null;
        try {
            while (qu.isEmpty()) {
                System.out.println("Queue Empty!!");
                wait();
            }
            element = qu.poll();
            System.out.println("Polled element: " + element);
            notifyAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return element;
    }
}
