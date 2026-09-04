package com.shivam151990.patterns.mediator.chat;

public interface Mediator {
    void sendMessage(User user, String message);
    void addUser(User user);
}
