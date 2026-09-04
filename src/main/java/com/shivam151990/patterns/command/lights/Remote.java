package com.shivam151990.patterns.command.lights;

public class Remote {

    private SwitchCommand command;

    public Remote(SwitchCommand command) {
        this.command = command;
    }

    public void runCommand() {
        command.execute();
    }
}
