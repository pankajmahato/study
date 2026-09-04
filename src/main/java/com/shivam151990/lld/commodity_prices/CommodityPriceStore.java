package com.shivam151990.lld.commodity_prices;

interface CommodityPriceStore {
    void upsert(int timestamp, int price);
    int getMaxCommodityPrice();
}
