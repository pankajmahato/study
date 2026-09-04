package com.shivam151990.lld.commodity_prices;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// ---- Strategy 1: HashMap + max variable ----
class HashMapMaxPriceStore implements CommodityPriceStore {
    private final Map<Integer, Integer> priceMap = new HashMap<>();
    private int maxPrice = Integer.MIN_VALUE;

    @Override
    public void upsert(int timestamp, int price) {
        // Update map
        Integer oldPrice = priceMap.put(timestamp, price);

        if (oldPrice == null) {
            // New entry
            maxPrice = Math.max(maxPrice, price);
        } else {
            // Existing timestamp updated
            if (price >= maxPrice) {
                maxPrice = price;
            } else if (oldPrice == maxPrice) {
                // Old max overwritten -> recalc
                maxPrice = Collections.max(priceMap.values());
            }
        }
    }

    @Override
    public int getMaxCommodityPrice() {
        return maxPrice == Integer.MIN_VALUE ? -1 : maxPrice;
    }
}

