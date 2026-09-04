package com.shivam151990.patterns.bridge.shape;

public class Main {
    public static void main(String[] args) {
        Shape s1 = new Rectangle(new RedColor());
        s1.draw();
        s1.draw();
    }
}
