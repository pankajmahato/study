package com.shivam151990.patterns.command.rooms;

import com.shivam151990.patterns.command.rooms.command.SwitchLightsCommand;

public class HouseRunner {
    public static void main(String[] args) {
        /*
         * Not appropriate
         */
        House house = new House();
        house.addRoom(new BathRoom());
        house.addRoom(new BedRoom());
        house.getRooms().forEach(Room::switchLights);

        /*
         * Better way since we are decoupling the classes that invoke the operation
         * from the classes that perform the operation.
         * The configuration option is also now with the client
         */
        Room bedRoom = new BedRoom();
        Light light = new Light();
        bedRoom.setCommand(new SwitchLightsCommand(light));
        bedRoom.executeCommand();

    }
}
