package com.shivam151990.lld.digital_wallet.repo;

import com.shivam151990.lld.digital_wallet.model.Wallet;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWalletRepo implements WalletRepo {
    private final Map<UUID, Wallet> m;

    public InMemoryWalletRepo() {
        m = new ConcurrentHashMap<>();
    }

    public void save(Wallet w) {
        m.put(w.getId(), w);
    }

    public Wallet find(UUID id) {
        return m.get(id);
    }
}
