package com.shivam151990.lld.ratelimiter.ratelimiter_practice;

public class FixedWindowGlobal implements IRateLimiter {

    private int maxWinInSec;
    private int maxReq;
    private long winStart;
    private int curReq;

    public FixedWindowGlobal(int maxWinInSec, int maxReq) {
        this.maxWinInSec = maxWinInSec;
        this.maxReq = maxReq;
        this.winStart = System.currentTimeMillis();
        this.curReq =0;

    }

    @Override
    public boolean allowRequest() {
        long curTs = System.currentTimeMillis();
        if (curTs - winStart >= maxWinInSec) {
            curTs = 0;
            winStart = curTs;
        }
        if (curReq < maxReq) {
            curReq++;
            return true;
        }
        return false;
    }
}
