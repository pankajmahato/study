package com.shivam151990.patterns.mediator.atc;

public class Runner {
    public static void main(String[] args) {
        // Crate Mediator
        ATCMediator mediator = new ATCMediatorImpl();

        // Create AirCraft that send and receive message via mediator
        AirCraft boeing1 = new AirCraftImpl(mediator, "boeing1");
        AirCraft helicoptor = new AirCraftImpl(mediator, "helicopter");

        // Register Aircraft to Mediator
        mediator.addAirCraft(boeing1);
        mediator.addAirCraft(helicoptor);

        //Send message via mediator
        boeing1.send("Hello from Boeing");
    }
}
