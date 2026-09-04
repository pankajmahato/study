package com.shivam151990.patterns.command.lights;

public class TurnOffCommand implements SwitchCommand {

    private Light light;

    public TurnOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOffLight();
    }
}
