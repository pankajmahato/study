package com.shivam151990.patterns.abstractfactory;

public class MacCheckBox implements CheckBox {
    @Override
    public void paint() {
        System.out.println("Mac style checkbox");
    }
}
