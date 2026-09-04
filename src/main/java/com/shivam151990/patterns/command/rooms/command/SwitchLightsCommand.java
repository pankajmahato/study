package com.shivam151990.patterns.command.rooms.command;

import com.shivam151990.patterns.command.rooms.Light;

public class SwitchLightsCommand implements Command {

    private Light light;

    public SwitchLightsCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.switchLights();
    }
}
