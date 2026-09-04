package com.shivam151990.lld.merge_intervals;

import lombok.Getter;

public class ShiftInterval {

    @Getter
    private int start;

    @Getter
    private int end;

    public ShiftInterval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getDuration() {
        return end - start;
    }
}
