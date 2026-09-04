package com.shivam151990.patterns.command.rooms;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Light {

    private boolean switchedOn;

    /*
        Light Switching logic moved here.
     */
    public void switchLights() {
        switchedOn = !switchedOn;
    }
}
