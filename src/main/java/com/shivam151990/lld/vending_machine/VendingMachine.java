package com.shivam151990.lld.vending_machine;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendingMachine implements VendingMachineState {

    @Getter
    @Setter
    private VendingMachineState currentState;

    @Getter
    private final List<Product> inventory;

    @Getter
    @Setter
    private double currentBalance;

    @Getter
    @Setter
    private Product selectedProduct;

    public VendingMachine() {
        currentState = new IdleState(this);
        inventory = new ArrayList<>();
        currentBalance = 0.0;
    }

    public void addProducts(List<Product> products) {
        inventory.addAll(products);
    }

    public void reset() {
        currentBalance = 0.0;
        selectedProduct = null;
        currentState = new IdleState(this);
    }

    public Optional<Product> getInventoryForProduct(String productId) {
        return inventory.stream()
                .filter(p -> p.getCode().equals(productId))
                .filter(p -> p.getQuantity() > 0)
                .findFirst();
    }

    @Override
    public void selectProduct(String productName) {
        currentState.selectProduct(productName);
    }

    @Override
    public boolean insertMoney(double amount) {
        return currentState.insertMoney(amount);
    }

    @Override
    public void dispense() {
        currentState.dispense();
    }
}
