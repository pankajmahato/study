package com.shivam151990.multithreading.producerconsumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BlockingQueue<T> {

    private Queue<T> qu;
    private final Lock lock;
    private final Condition condition;
    private final int maxSize;

    public BlockingQueue(int size) {
        maxSize = size;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        qu = new LinkedList<>();
    }


    public void put(T ele) {
        lock.lock();
        try {
            while (qu.size() == maxSize) {
                System.out.println("WAITING!!! QUEUE FULL!!!");
                condition.await();
            }
            qu.offer(ele);
            condition.signalAll();
        } catch (RuntimeException | InterruptedException ignored) {}
        finally {
            lock.unlock();
        }
    }

    public T take() {
        T ele = null;
        lock.lock();
        try {
            while (qu.isEmpty()) {
                System.out.println("WAITING!!!! QUEUE EMPTY!!!");
                condition.await();
            }
            ele = qu.poll();
            condition.signalAll();
        } catch (RuntimeException | InterruptedException ignored) {}
        finally {
            lock.unlock();
        }
        return ele;
    }
}

public class ProducerConsumerSingleLock{
    public static void main(String[] args) {
        final int[] count = {0};
        BlockingQueue<Integer> qu = new BlockingQueue<>(10);
        Thread t1 = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("PUT : " + count[0]);
                qu.put(count[0]);
                count[0]++;
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("GET : " + qu.take());
            }
        });

        t1.start();
        t2.start();
    }
}
