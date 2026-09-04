package com.shivam151990.lld.digital_wallet.model;

import com.shivam151990.lld.digital_wallet.service.PaymentMethod;
import com.shivam151990.lld.digital_wallet.service.WalletService;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class ExternalTransferTransaction extends Transaction {

    @Getter
    private final Wallet wallet;
    @Getter
    private final PaymentMethod method;
    @Getter
    private final Direction direction;

    public enum Direction { TOP_UP, WITHDRAWAL }

    public ExternalTransferTransaction(Wallet wallet, PaymentMethod method, BigDecimal amount, Direction dir) {
        super(amount);
        this.wallet     = wallet;
        this.method     = method;
        this.direction  = dir;
    }

    @Override
    public void process(WalletService svc) {
        if (direction == Direction.TOP_UP) {
            method.authorize(amount);
            wallet.credit(amount);
        } else if (direction == Direction.WITHDRAWAL){
            wallet.debit(amount);
            method.authorize(amount);
        } else {
            throw new RuntimeException("Illegal operation");
        }
        this.status = TransactionStatus.COMPLETED;
    }

    @Override
    public Set<UUID> involvedWallets() {
        return Set.of(wallet.getId());
    }

    @Override
    public String toString() {
        return "ExternalTransferTransaction {" +
                "wallet=" + wallet.getOwner().getName() +
                ", method=" + method.getLabel() +
                ", dir=" + direction +
                ", amount=" + amount +
                ", status=" + status +
                " }";
    }
}
