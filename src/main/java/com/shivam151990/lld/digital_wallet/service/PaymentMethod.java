package com.shivam151990.lld.digital_wallet.service;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public abstract class PaymentMethod {
    private final UUID id;
    private final String label;

    public PaymentMethod(String label) {
        this.id = UUID.randomUUID();
        this.label = label;
    }

    public abstract void authorize(BigDecimal amount);
}
