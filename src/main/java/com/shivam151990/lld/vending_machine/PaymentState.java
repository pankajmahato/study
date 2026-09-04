package com.shivam151990.lld.vending_machine;

public class PaymentState implements VendingMachineState {

    private final VendingMachine vendingMachine;

    public PaymentState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(String productName) {
        System.out.println(vendingMachine.getSelectedProduct().getName()
                + " Already selected please make payment or cancel transaction");
    }

    @Override
    public boolean insertMoney(double amount) {
        Product selectedProduct = vendingMachine.getSelectedProduct();
        if (amount < selectedProduct.getPrice()) {
            System.out.println("Insufficient amount given");
            return false;
        }
        if (!canProvideChange(selectedProduct.getPrice())) {
            System.out.println("Machine cannot provide change. Sorry for the inconvenience!!!");
            return false;
        }
        double remainingChange = vendingMachine.getCurrentBalance() - selectedProduct.getPrice();
        vendingMachine.setCurrentBalance(remainingChange);
        vendingMachine.setCurrentState(new DispenseState(vendingMachine));
        System.out.printf("Amount Accepted. Returning change: %f \n", amount - selectedProduct.getPrice());
//        stop();
        return true;
    }

    private boolean canProvideChange(double amountGiven) {
        return vendingMachine.getCurrentBalance() >= amountGiven;
    }

    @Override
    public void dispense() {
        System.out.println("Cannot dispense please make Payment first !!!");
    }
}
