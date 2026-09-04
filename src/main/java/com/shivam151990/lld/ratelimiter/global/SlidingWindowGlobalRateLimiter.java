package com.shivam151990.lld.ratelimiter.global;

import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowGlobalRateLimiter {
    private final long windowSize; // Size of the window in milliseconds
    private final int maxRequests; // Maximum number of requests allowed in the window
    private final Queue<Long> requestTimestamps; // Timestamps of requests in the current window

    public SlidingWindowGlobalRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
        this.requestTimestamps = new LinkedList<>();
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // Remove timestamps older than the current window
        while (!requestTimestamps.isEmpty() && currentTime - requestTimestamps.peek() > windowSize) {
            requestTimestamps.poll();
        }
        // Check if the request count is within the limit
        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.offer(currentTime); // Record the current request timestamp
            return true; // Allow the request
        }
        return false; // Reject the request
    }
}
