package com.shivam151990.lld.ratelimiter.ratelimiter_practice;

public class TokenBucketGlobal implements IRateLimiter {

    private long maxTokens;
    private long curTokens;
    private long lastRefillTime;
    private long refillWindowTimePerToken;

    public TokenBucketGlobal(long maxTokens) {
        this.maxTokens = maxTokens;
        this.curTokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
        this.refillWindowTimePerToken = 1000;
    }

    @Override
    public boolean allowRequest() {
        refill();
        if (curTokens < maxTokens) {
            curTokens++;
            return true;
        }
        return false;
    }

    private void refill() {
        long curTs = System.currentTimeMillis();
        long elapsed = curTs - lastRefillTime;

        if (elapsed >= refillWindowTimePerToken) {
            long tokensToFill = elapsed / refillWindowTimePerToken;
            curTokens = Math.min(maxTokens, curTokens + tokensToFill);
            lastRefillTime = curTs;
        }
    }
}
