package com.shivam151990.patterns.state.simple_atm;

public interface ATMState {
    // Operation for the ATM
    void withdraw(int amount);
    void deposit(int amount);
}
