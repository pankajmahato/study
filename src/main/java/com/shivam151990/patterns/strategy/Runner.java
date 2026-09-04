package com.shivam151990.patterns.strategy;

public class Runner {

    public static void main(String[] args) {

        ShoppingCart cart = new ShoppingCart();

        Product pants = new Product("234", 25);
        Product shirt = new Product("987", 15);


        cart.addProduct(pants);
        cart.addProduct(shirt);

        //payment decisions
        cart.pay(new PaypalAlgorithm("shivam@gmail.com", "pass"));


        cart.pay(new CreditCardAlgorithm("shivam", "238756464"));


    }
}
