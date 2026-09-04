package com.shivam151990.lld.digital_wallet.repo;

import com.shivam151990.lld.digital_wallet.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryTxRepo implements TxRepo {
    private final List<Transaction> tx;

    public InMemoryTxRepo() {
        tx = new ArrayList<>();
    }

    public void save(Transaction t) {
        tx.add(t);
    }
    public List<Transaction> findTransactions(UUID walletId) {
        return tx.stream()
                .filter(t -> t.involvedWallets().contains(walletId))
                .toList();
    }
}
