package com.shivam151990.lld.shopping_cart;

import java.util.List;
import java.util.Map;

public class QuantityCheckRule implements Rule {

    private Map<String, Integer> maxQuantityMap;

    public QuantityCheckRule(Map<String, Integer> maxQuantityMap) {
        this.maxQuantityMap = maxQuantityMap;
    }

    @Override
    public boolean check(List<Product> products) {
        for (Product p: products) {
            if (maxQuantityMap.containsKey(p.getCategory())
                    && maxQuantityMap.get(p.getCategory()) > p.getQuantity()) {
                return false;
            }
        }
        return true;
    }
}
