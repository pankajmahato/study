package com.shivam151990.lld.shopping_cart;

public class Product {
    private final String category;
    private final int quantity;

    public Product(String category, int quantity) {
        this.category = category;
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }
}
