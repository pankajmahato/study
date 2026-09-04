package com.shivam151990.lld.ratelimiter.global;

import com.shivam151990.lld.ratelimiter.ratelimiter_practice.IRateLimiter;

public class LeakyBucketGlobalRateLimiter implements IRateLimiter {
    private final int capacity;           // Max number of requests bucket can hold
    private final int leakRatePerSecond;  // Requests leaked per second (processed)
    private int currentRequests;          // Current requests in bucket
    private long lastLeakTime;            // Last time bucket leaked

    public LeakyBucketGlobalRateLimiter(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
        this.currentRequests = 0;
        this.lastLeakTime = System.currentTimeMillis();
    }

    // Simulate leaking of requests over time
    private void leak() {
        long now = System.currentTimeMillis();
        long elapsedSeconds = (now - lastLeakTime) / 1000;

        if (elapsedSeconds > 0) {
            int leakedRequests = (int) (elapsedSeconds * leakRatePerSecond);
            currentRequests = Math.max(0, currentRequests - leakedRequests);
            lastLeakTime = now;
        }
    }

    // Try to add a request
    public boolean allowRequest() {
        leak();
        if (currentRequests < capacity) {
            currentRequests++;
            return true; // Request accepted
        }
        return false; // Request rejected (bucket full)
    }
}

