package com.shivam151990.lld.task_manager.model;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String name;
    private final String email;

    public User(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
