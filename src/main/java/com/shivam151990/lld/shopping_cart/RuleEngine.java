package com.shivam151990.lld.shopping_cart;

import java.util.*;

class RuleEngine {
    private final Map<String, Rule> rules;

    public RuleEngine() {
        rules = new HashMap<>();
    }

    public void addRule(String ruleName, Rule rule) {
        rules.put(ruleName, rule);
    }

    public boolean validateCart(ShoppingCart cart) {
        for (Rule r: rules.values()) {
            if (!r.check(cart.getProducts())) {
                return false;
            }
        }
        return true;
    }
}