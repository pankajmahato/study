package com.shivam151990.patterns.command.lights;

/**
 * Turns a request or a behaviour into a standalone object that contains
 * everything about the request and encapsulates all the relevant info needed to performa action
 */
public interface SwitchCommand {
    void execute();
}
