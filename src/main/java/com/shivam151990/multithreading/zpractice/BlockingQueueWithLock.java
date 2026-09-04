package com.shivam151990.multithreading.zpractice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueWithLock<T> {

    private T element;
    private final int maxCapacity;
    private Queue<T> qu;
    private Lock lock;
    private Condition notEmpty;
    private Condition notFull;

    public BlockingQueueWithLock(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.qu = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    public void offer(T ele) {
        lock.lock();
        try {
            while(qu.size() == maxCapacity) {
                System.out.println("Queue Full!!");
                notFull.await();
            }
            System.out.println("Added element: " + ele);
            qu.offer(ele);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public T poll() {
        lock.lock();
        T element = null;
        try {
            while (qu.isEmpty()) {
                System.out.println("Queue Empty!!");
                notEmpty.await();
            }
            element = qu.poll();
            System.out.println("Polled element: " + element);
            notFull.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return element;
    }
}
