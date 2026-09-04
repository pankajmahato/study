package com.shivam151990.lld.digital_wallet.repo;

import com.shivam151990.lld.digital_wallet.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TxRepo {
    void save(Transaction t);

    List<Transaction> findTransactions(UUID walletId);
}
