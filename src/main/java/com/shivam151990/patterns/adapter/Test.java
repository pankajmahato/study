package com.shivam151990.patterns.adapter;

public class Test {

    public static void main(String[] args) {
        WeighingMachine weighingMachine = new WeighingMachineImpl();
        WeightMachineAdapter weightMachineAdapter = new WeighingMachineAdapterImpl(weighingMachine);
        System.out.println(weightMachineAdapter.getWeightInKg());
    }
}
