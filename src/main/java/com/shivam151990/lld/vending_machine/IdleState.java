package com.shivam151990.lld.vending_machine;

import java.util.Optional;

public class IdleState implements VendingMachineState {

    private final VendingMachine vendingMachine;

    public IdleState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(String productName) {
        Optional<Product> product = vendingMachine.getInventoryForProduct(productName);
        if (product.isEmpty() || product.get().getQuantity() <= 0) {
            System.out.println(productName + " empty !!!");
            return;
        }
        Product p = product.get();
        vendingMachine.setSelectedProduct(p);
        vendingMachine.setCurrentState(new PaymentState(vendingMachine));
        System.out.println(productName + " selected please deposit amount " + p.getPrice());
//        stop();
    }

    @Override
    public boolean insertMoney(double amount) {
        System.out.println("Cannot insert money before selecting product");
        return false;
    }

    @Override
    public void dispense() {
        System.out.println("Cannot dispense before selecting product & making payment");
    }
}
