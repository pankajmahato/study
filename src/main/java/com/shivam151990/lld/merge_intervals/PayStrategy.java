package com.shivam151990.lld.merge_intervals;

import java.util.List;

public interface PayStrategy {
    int pay(List<ShiftInterval> shifts);
}
