package com.shivam151990.patterns.mediator.atc;

public interface ATCMediator {
    void sendMessage(String message, AirCraft fromAirCraft);
    void addAirCraft(AirCraft airCraft);
}
