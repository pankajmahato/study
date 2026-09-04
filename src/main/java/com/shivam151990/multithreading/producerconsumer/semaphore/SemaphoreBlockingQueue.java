package com.shivam151990.multithreading.producerconsumer.semaphore;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreBlockingQueue<T> {

    private final Queue<T> queue;

    private final Semaphore notFull;   // Counts free slots, initially maxCapacity
    private final Semaphore notEmpty;  // Counts elements, initially 0

    private final ReentrantLock lock;  // Protects access to queue

    public SemaphoreBlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.notFull = new Semaphore(capacity);
        this.notEmpty = new Semaphore(0);
        this.lock = new ReentrantLock();
    }

    public void offer(T element) {
        try {
            notFull.acquire();
            lock.lock();
            queue.offer(element);
            System.out.println("Added element: " + element);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        } finally {
            lock.unlock();
            notEmpty.release();
        }
    }

    public T poll() {
        T element;
        try {
            notEmpty.acquire();
            lock.lock();
            element = queue.poll();
            System.out.println("Removed Element: " + element);
            return element;
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            lock.unlock();
            notFull.release();
        }
    }
}
