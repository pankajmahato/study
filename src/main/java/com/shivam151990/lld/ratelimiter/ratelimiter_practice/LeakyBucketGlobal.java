package com.shivam151990.lld.ratelimiter.ratelimiter_practice;

public class LeakyBucketGlobal implements IRateLimiter {

    private long maxBucketCapacity;
    private long curReq;
    private long leakRatePerSec;
    private long lastLeakTime;

    public LeakyBucketGlobal(long leakRatePerSec, long maxBucketCapacity) {
        this.leakRatePerSec = leakRatePerSec;
        this.maxBucketCapacity = maxBucketCapacity;
        this.curReq = 0;
        lastLeakTime = System.currentTimeMillis();
    }

    @Override
    public boolean allowRequest() {
        leak();
        if (curReq > maxBucketCapacity) {
            curReq++;
            return true;
        }
        return false;
    }

    private void leak() {
        long curTs = System.currentTimeMillis();
        long elapsedTimeInSec = (curTs - lastLeakTime) / 1000;

        if (elapsedTimeInSec > 0) {
            long leakedRequest = elapsedTimeInSec * leakRatePerSec;
            curReq = Math.max(0, curReq - leakedRequest);
            lastLeakTime = curTs;
        }
    }
}
