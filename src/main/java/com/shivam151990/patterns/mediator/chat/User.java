package com.shivam151990.patterns.mediator.chat;

public abstract class User {

    protected Mediator mediator;
    protected String name;

    protected User(Mediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    abstract void sendMessage(String message);

    abstract void receiveMessage(String message);
}
