package net.refination.refinecraft.spawn;

import net.refination.refinecraft.InterfaceRefineCraftModule;
import net.refination.refinecraft.settings.Spawns;
import net.refination.refinecraft.storage.AsyncStorageObjectHolder;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SpawnStorage extends AsyncStorageObjectHolder<Spawns> implements InterfaceRefineCraftModule {
    public SpawnStorage(final InterfaceRefineCraft RC) {
        super(RC, Spawns.class);
        reloadConfig();
    }

    @Override
    public File getStorageFile() {
        return new File(RC.getDataFolder(), "spawn.yml");
    }

    @Override
    public void finishRead() {
    }

    @Override
    public void finishWrite() {
    }

    public void setSpawn(final Location loc, final String group) {
        acquireWriteLock();
        try {
            if (getData().getSpawns() == null) {
                getData().setSpawns(new HashMap<String, Location>());
            }
            getData().getSpawns().put(group.toLowerCase(Locale.ENGLISH), loc);
        } finally {
            unlock();
        }

        if ("default".equalsIgnoreCase(group)) {
            loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
    }

    public Location getSpawn(final String group) {
        acquireReadLock();
        try {
            if (getData().getSpawns() == null || group == null) {
                return getWorldSpawn();
            }
            final Map<String, Location> spawnMap = getData().getSpawns();
            String groupName = group.toLowerCase(Locale.ENGLISH);
            if (!spawnMap.containsKey(groupName)) {
                groupName = "default";
            }
            if (!spawnMap.containsKey(groupName)) {
                return getWorldSpawn();
            }
            return spawnMap.get(groupName);
        } finally {
            unlock();
        }
    }

    private Location getWorldSpawn() {
        for (World world : RC.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.NORMAL) {
                continue;
            }
            return world.getSpawnLocation();
        }
        return RC.getServer().getWorlds().get(0).getSpawnLocation();
    }
}
