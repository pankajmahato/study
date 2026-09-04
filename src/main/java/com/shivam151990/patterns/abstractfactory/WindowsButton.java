package com.shivam151990.patterns.abstractfactory;

public class WindowsButton implements Button {
    @Override
    public void paint() {
        System.out.println("Windows Paint button");
    }
}
