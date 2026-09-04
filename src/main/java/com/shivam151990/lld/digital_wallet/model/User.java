package com.shivam151990.lld.digital_wallet.model;

import lombok.Getter;

import java.util.UUID;

public class User {
    @Getter
    private final UUID id;

    @Getter
    private String name;

    @Getter
    private String email;

    public User(String name, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
    }
}
