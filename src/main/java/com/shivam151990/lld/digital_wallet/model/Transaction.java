package com.shivam151990.lld.digital_wallet.model;

import com.shivam151990.lld.digital_wallet.service.WalletService;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public abstract class Transaction {

    @Getter
    protected final UUID id;
    @Getter
    protected final Instant timestamp;
    @Getter
    protected final BigDecimal amount;
    @Getter
    protected TransactionStatus status;

    public Transaction(BigDecimal amount) {
        this.amount = amount;
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.status = TransactionStatus.PENDING;
    }

    public abstract void process(WalletService walletService);

    public abstract Set<UUID> involvedWallets();
}
