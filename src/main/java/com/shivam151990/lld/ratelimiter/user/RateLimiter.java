package com.shivam151990.lld.ratelimiter.user;

public interface RateLimiter {
    boolean tryAcquire(String userId);
}
