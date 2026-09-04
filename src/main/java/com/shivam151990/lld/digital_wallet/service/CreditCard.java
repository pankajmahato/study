package com.shivam151990.lld.digital_wallet.service;

import java.math.BigDecimal;

public class CreditCard extends PaymentMethod {
    private final String number;

    public CreditCard(String label, String number) {
        super(label);
        this.number = number;
    }
    public void authorize(BigDecimal amount) { }
}
