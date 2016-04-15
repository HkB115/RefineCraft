package net.refination.refinecraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class PlayerTarget implements InterfaceTarget {
    private final String name;

    public PlayerTarget(Player entity) {
        this.name = entity.getName();
    }

    @Override
    public Location getLocation() {
        return Bukkit.getServer().getPlayerExact(name).getLocation();
    }
}