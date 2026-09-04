package com.shivam151990.lld.digital_wallet.service;

import com.shivam151990.lld.digital_wallet.model.*;
import com.shivam151990.lld.digital_wallet.repo.TxRepo;
import com.shivam151990.lld.digital_wallet.repo.WalletRepo;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.NoSuchElementException;
import java.util.UUID;

public class WalletService {

    private final WalletRepo wRepo;
    private final TxRepo txRepo;

    public WalletService(WalletRepo walletRepo, TxRepo txRepo) {
        this.wRepo = walletRepo;
        this.txRepo = txRepo;
    }

    public Wallet createWallet(User user, Currency currency) {
        Wallet wallet = new Wallet(user, currency);
        wRepo.save(wallet);
        return wallet;
    }

    public void doTransfer(Wallet from, Wallet to, BigDecimal amt) {
        from.debit(amt);
        to.credit(amt);
    }

    public Transaction transfer(UUID fromId, UUID toId, BigDecimal amount) {
        // Find wallets
        Wallet from = wRepo.find(fromId);
        Wallet to = wRepo.find(toId);

        // Create and process transaction
        Transaction tx = new WalletTransferTransaction(from, to, amount);
        tx.process(this);

        // Save transaction in repo
        txRepo.save(tx);

        return tx;
    }

    public Transaction topUp(UUID walletId, UUID paymentMethodId, BigDecimal amount) {
        return performExternal(walletId, paymentMethodId, amount, ExternalTransferTransaction.Direction.TOP_UP);
    }

    public Transaction withdraw(UUID walletId, UUID paymentMethodId, BigDecimal amount) {
        return performExternal(walletId, paymentMethodId, amount, ExternalTransferTransaction.Direction.WITHDRAWAL);
    }

    private Transaction performExternal(UUID walletId, UUID paymentMethodId, BigDecimal amount, ExternalTransferTransaction.Direction direction) {
        Wallet w = wRepo.find(walletId);
        PaymentMethod pm = w.getPaymentMethods().stream()
                .filter(m -> m.getId().equals(paymentMethodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("PaymentMethod not found"));

        ExternalTransferTransaction tx = new ExternalTransferTransaction(w, pm, amount, direction);
        tx.process(this);
        txRepo.save(tx);
        return tx;
    }
}
