package com.shivam151990.patterns.state.atm;

public interface ATMState {
    void insertCard();
    void ejectCard();
    void insertPin(int pin);
    void requestCash(int amount);
}
