package com.shivam151990.lld.commodity_prices;

public class CommodityPriceDemo {
    public static void main(String[] args) {
        // Choose strategy dynamically
        CommodityPriceStore store1 = new HashMapMaxPriceStore();  // write-heavy
        CommodityPriceStore store2 = new PriorityQueueMaxPriceStore(); // read-heavy

        // Test both
        System.out.println("Testing HashMap strategy:");
        testStore(store1);

        System.out.println("\nTesting PriorityQueue strategy:");
        testStore(store2);
    }

    private static void testStore(CommodityPriceStore store) {
        store.upsert(1, 100);
        store.upsert(2, 120);
        store.upsert(3, 90);
        System.out.println("Max after 3 inserts: " + store.getMaxCommodityPrice()); // 120

        store.upsert(2, 80); // overwrite timestamp 2
        System.out.println("Max after updating ts=2 to 80: " + store.getMaxCommodityPrice()); // 100

        store.upsert(4, 200);
        System.out.println("Max after adding ts=4=200: " + store.getMaxCommodityPrice()); // 200
    }
}
