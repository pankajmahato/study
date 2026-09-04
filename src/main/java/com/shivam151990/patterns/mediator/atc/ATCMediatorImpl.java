package com.shivam151990.patterns.mediator.atc;

import java.util.ArrayList;
import java.util.List;

public class ATCMediatorImpl implements ATCMediator {

    private List<AirCraft> airCrafts;

    public ATCMediatorImpl() {
        this.airCrafts = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message, AirCraft fromAirCraft) {
        for (AirCraft airCraft: airCrafts) {
            // AirCraft sending message is skipped
            if (airCraft != fromAirCraft) {
                airCraft.receive(message);
            }
        }
    }

    @Override
    public void addAirCraft(AirCraft airCraft) {
        airCrafts.add(airCraft);
    }
}
