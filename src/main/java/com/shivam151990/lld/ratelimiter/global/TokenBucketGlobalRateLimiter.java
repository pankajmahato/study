package com.shivam151990.lld.ratelimiter.global;

import com.shivam151990.lld.ratelimiter.ratelimiter_practice.IRateLimiter;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketGlobalRateLimiter implements IRateLimiter {
    private final long maxTokens;        // bucket capacity
    private final long refillWindowTimePerToken; // refill interval (ms)
    private final AtomicLong tokens;      // current tokens
    private volatile long lastRefill;     // last refill time

    public TokenBucketGlobalRateLimiter(long maxTokens) {
        this.refillWindowTimePerToken = 1000;
        this.maxTokens = maxTokens;
        this.tokens = new AtomicLong(maxTokens);
        this.lastRefill = System.currentTimeMillis();
    }

    @Override
    public boolean allowRequest() {
        refill();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefill;

        if (elapsed >= refillWindowTimePerToken) {
            long newTokens = (elapsed / refillWindowTimePerToken);
            tokens.set(Math.min(maxTokens, tokens.get() + newTokens));
            lastRefill = now;
        }
    }
}
