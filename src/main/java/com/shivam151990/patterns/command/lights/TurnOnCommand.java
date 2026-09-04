package com.shivam151990.patterns.command.lights;

public class TurnOnCommand implements SwitchCommand {

    private Light light;

    public TurnOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOnLight();
    }
}
