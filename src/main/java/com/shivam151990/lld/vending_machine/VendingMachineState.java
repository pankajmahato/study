package com.shivam151990.lld.vending_machine;

/**
  This Interface method denotes actions a vending machine can take
*/
public interface VendingMachineState {
    void selectProduct(String productName);
    boolean insertMoney(double amount);
    void dispense();
}
