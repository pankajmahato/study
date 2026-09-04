package com.shivam151990.patterns.state.television;

public class TvOnState implements TvState {

    @Override
    public void doAction() {
        System.out.println("TV is turned ON");
    }
}
