package com.shivam151990.lld.vending_machine;

public class DispenseState implements VendingMachineState {

    private final VendingMachine vendingMachine;

    public DispenseState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(String productName) {
        System.out.println("Cannot select product until current "
                + vendingMachine.getSelectedProduct().getName() + " dispensed");
    }

    @Override
    public boolean insertMoney(double amount) {
        System.out.println("Money already inserted to get product "
                + vendingMachine.getSelectedProduct().getName());
        return false;
    }

    @Override
    public void dispense() {
        Product currentSelectedProduct = vendingMachine.getSelectedProduct();
        currentSelectedProduct.reduceQuantity();
        vendingMachine.setCurrentState(new IdleState(vendingMachine));
        System.out.println("Product " + currentSelectedProduct.getName()
                + " dispensed and change given. Enjoy!!!");
//        stop();
    }
}
