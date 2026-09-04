package com.shivam151990.lld.digital_wallet.service;

import java.math.BigDecimal;

public class BankAccount extends PaymentMethod {
    private final String account;
    public BankAccount(String label, String account) {
        super(label);
        this.account = account;
    }
    public void authorize(BigDecimal amount) { }
}
