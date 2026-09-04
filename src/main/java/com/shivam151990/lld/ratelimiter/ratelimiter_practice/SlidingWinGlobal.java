package com.shivam151990.lld.ratelimiter.ratelimiter_practice;

import java.util.LinkedList;

public class SlidingWinGlobal implements IRateLimiter {

    private int maxWin;
    private int maxReq;
    private LinkedList<Long> requests;

    public SlidingWinGlobal(int maxWin, int maxReq) {
        this.maxWin = maxWin;
        this.maxReq = maxReq;
        this.requests = new LinkedList<>();
    }

    @Override
    public boolean allowRequest() {
        long curTs = System.currentTimeMillis();

        while (!requests.isEmpty() && curTs - requests.peek() > maxWin) {
            requests.poll();
        }
        if (requests.size() < maxReq) {
            requests.offer(curTs);
            return true;
        }
        return false;
    }
}
