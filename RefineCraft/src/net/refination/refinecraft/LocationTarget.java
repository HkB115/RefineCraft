package net.refination.refinecraft;

import org.bukkit.Location;


public class LocationTarget implements InterfaceTarget {
    private final Location location;

    LocationTarget(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}