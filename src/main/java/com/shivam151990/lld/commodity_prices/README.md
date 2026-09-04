## We need to design a system to store and query commodity prices:

1.) Each entry is <timestamp, commodityPrice>.

2.) Timestamps may:
    * Arrive out of order.
    * Have duplicates → in which case, we update the price at that timestamp.

3.) The system should support:
    * upsert(timestamp, price) → Insert or update the price for a timestamp.
    * getMaxCommodityPrice() → Return the maximum commodity price seen so far.

4.) Optimize for frequent reads and writes.

5.) If possible, make getMaxCommodityPrice() run in O(1) time using a variable to track the current max.