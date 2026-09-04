package com.shivam151990.lld.digital_wallet.model;

import com.shivam151990.lld.digital_wallet.service.PaymentMethod;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Wallet {
    @Getter
    private final UUID id;
    @Getter
    private final User owner;
    private BigDecimal balance;
    private final List<PaymentMethod> methods;
    private final ReentrantLock lock;
    @Getter
    private final Currency currency;

    public Wallet(User owner, Currency currency) {
        this.id = UUID.randomUUID();
        this.lock = new ReentrantLock();
        this.currency = currency;
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
        this.methods = new CopyOnWriteArrayList<>();
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public void addPaymentMethod(PaymentMethod m) {
        methods.add(m);
    }

    public void removePaymentMethod(UUID methodId) {
        methods.removeIf(m -> m.getId().equals(methodId));
    }

    public List<PaymentMethod> getPaymentMethods() {
        return List.copyOf(methods);
    }

    public void credit(BigDecimal amount) {
        lock.lock();
        try {
            balance = balance.add(amount);
        } finally {
            lock.unlock();
        }
    }

    public void debit(BigDecimal amount) {
        lock.lock();
        try {
            balance = balance.subtract(amount);
        } finally {
            lock.unlock();
        }
    }

    private void validateCurrency(Currency curr) {
        if (!currency.equals(curr)) {
            throw new IllegalArgumentException("Currency mismatch: wallet=" + currency + ", provided=" + curr);
        }
    }
}