package com.shivam151990.patterns.command.lights;

public class Main {
    public static void main(String[] args) {
        Light light = new Light();

        SwitchCommand command = new TurnOffCommand(light);
        command.execute();

        SwitchCommand command1 = new TurnOnCommand(light);
        command1.execute();

        Remote remote = new Remote(command);
        remote.runCommand();
    }
}
