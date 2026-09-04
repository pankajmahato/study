package com.shivam151990.lld.vending_machine;

public class UserVendingMachine {

    private VendingMachine vendingMachine;

    public UserVendingMachine(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    void selectProduct(String product) {
        vendingMachine.selectProduct(product);
    }

    void insertMoney(double amount) {
        if (vendingMachine.insertMoney(amount)) {
            vendingMachine.dispense();
        }
    }
}
