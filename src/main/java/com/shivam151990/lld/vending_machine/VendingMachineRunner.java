package com.shivam151990.lld.vending_machine;

import java.util.List;

public class VendingMachineRunner {

    public static void main(String[] args) {

        List<Product> products = List.of(
                new Product("p1", "chips", 10.0, 2),
                new Product("p3", "cola", 40.0, 2)
        );

        // Initialize Vending Machine
        VendingMachine machine = new VendingMachine();
        machine.addProducts(products);
        machine.setCurrentBalance(100);

        UserVendingMachine userVendingMachine = new UserVendingMachine(machine);

        // Dispense Product
//        userVendingMachine.selectProduct("p1");
//        userVendingMachine.insertMoney(40);

        // Inserting money before selecting product
        userVendingMachine.insertMoney(10.0);

    }
}
