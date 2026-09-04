package com.shivam151990.lld.digital_wallet.model;

import com.shivam151990.lld.digital_wallet.service.WalletService;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class WalletTransferTransaction extends Transaction {

    @Getter
    private final Wallet from;

    @Getter
    private final Wallet to;

    public WalletTransferTransaction(Wallet from, Wallet to, BigDecimal amount) {
        super(amount);
        this.from = from;
        this.to = to;
    }

    public void process(WalletService walletService) {
        walletService.doTransfer(from, to, amount);
        this.status = TransactionStatus.COMPLETED;
    }

    @Override
    public Set<UUID> involvedWallets() {
        return Set.of(from.getId(), to.getId());
    }

    @Override
    public String toString() {
        return "Transaction {" +
                "from=" + from.getOwner().getName() +
                ", to=" + to.getOwner().getName() +
                ", id=" + id +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", status=" + status +
                " }";
    }
}
