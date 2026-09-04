package com.shivam151990.lld.digital_wallet.repo;

import com.shivam151990.lld.digital_wallet.model.Wallet;

import java.util.UUID;

public interface WalletRepo {
    void save(Wallet w);

    Wallet find(UUID id);
}
