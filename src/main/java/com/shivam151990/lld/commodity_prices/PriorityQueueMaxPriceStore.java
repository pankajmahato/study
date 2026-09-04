package com.shivam151990.lld.commodity_prices;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// ---- Strategy 2: HashMap + PriorityQueue ----
class PriorityQueueMaxPriceStore implements CommodityPriceStore {
    private static class PriceEntry {
        int price;
        int timestamp;
        PriceEntry(int price, int timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }
    }

    private final Map<Integer, Integer> priceMap = new HashMap<>();
    private final PriorityQueue<PriceEntry> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(b.price, a.price) // max-heap
    );

    @Override
    public void upsert(int timestamp, int price) {
        priceMap.put(timestamp, price);
        maxHeap.offer(new PriceEntry(price, timestamp));
    }

    @Override
    public int getMaxCommodityPrice() {
        while (!maxHeap.isEmpty()) {
            PriceEntry top = maxHeap.peek();
            // Check if heap top is still valid
            if (priceMap.get(top.timestamp) == top.price) {
                return top.price;
            }
            maxHeap.poll(); // discard stale entry
        }
        return -1;
    }
}
