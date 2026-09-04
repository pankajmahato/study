package com.shivam151990.lld.ratelimiter.global;

public class FixedWindowGlobalRateLimiter {
    private final int maxRequests; // Maximum number of requests allowed in the window
    private final long windowSize; // Size of the window in milliseconds
    private int requestCount; // Number of requests in the current window
    private long windowStart; // Start time of the current window

    public FixedWindowGlobalRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
        this.windowStart = System.currentTimeMillis();
        this.requestCount = 0;
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // Check if the current window has expired
        if (currentTime - windowStart >= windowSize) {
            requestCount = 0; // Reset the request count
            windowStart = currentTime; // Start a new window
        }
        // Check if the request count is within the limit
        if (requestCount < maxRequests) {
            requestCount++; // Increment the request count
            return true; // Allow the request
        }
        return false; // Reject the request
    }
}
