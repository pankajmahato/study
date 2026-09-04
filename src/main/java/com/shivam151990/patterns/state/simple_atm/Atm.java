package com.shivam151990.patterns.state.simple_atm;

import lombok.Getter;
import lombok.Setter;

public class Atm implements ATMState {

    @Setter
    @Getter
    private int cashAvailable;
    private ATMState currentState;

    public Atm(int cashAvailable) {
        this.cashAvailable = cashAvailable;
        this.currentState = new Working(this);
    }

    public void setState(ATMState currentState) {
        this.currentState = currentState;
    }

    @Override
    public void withdraw(int amount) {
        currentState.withdraw(amount);
    }

    @Override
    public void deposit(int amount) {
        currentState.deposit(amount);
    }
}
