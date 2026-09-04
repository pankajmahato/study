package com.shivam151990.patterns.state.television;

public class Tv implements TvState {

    private TvState tvState;

    public void setState(TvState state) {
        this.tvState=state;
    }

    public TvState getState() {
        return this.tvState;
    }

    @Override
    public void doAction() {
        this.tvState.doAction();
    }
}
