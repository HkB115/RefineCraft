package net.refination.refinecraft.spawn;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class RefineCraftSpawn extends JavaPlugin implements InterfaceRefineCraftSpawn {
    private static final Logger LOGGER = Bukkit.getLogger();
    private transient InterfaceRefineCraft RC;
    private transient SpawnStorage spawns;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = getServer().getPluginManager();
        RC = (InterfaceRefineCraft) pluginManager.getPlugin("RefineCraft");
        if (!this.getDescription().getVersion().equals(RC.getDescription().getVersion())) {
            LOGGER.log(Level.WARNING, tl("versionMismatchAll"));
        }
        if (!RC.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        spawns = new SpawnStorage(RC);
        RC.addReloadListener(spawns);

        final RefineCraftSpawnPlayerListener playerListener = new RefineCraftSpawnPlayerListener(RC, spawns);
        pluginManager.registerEvent(PlayerRespawnEvent.class, playerListener, RC.getSettings().getRespawnPriority(), new EventExecutor() {
            @Override
            public void execute(final Listener ll, final Event event) throws EventException {
                ((RefineCraftSpawnPlayerListener) ll).onPlayerRespawn((PlayerRespawnEvent) event);
            }
        }, this);
        pluginManager.registerEvent(PlayerJoinEvent.class, playerListener, RC.getSettings().getRespawnPriority(), new EventExecutor() {
            @Override
            public void execute(final Listener ll, final Event event) throws EventException {
                ((RefineCraftSpawnPlayerListener) ll).onPlayerJoin((PlayerJoinEvent) event);
            }
        }, this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        return RC.onCommandRefineCraft(sender, command, commandLabel, args, RefineCraftSpawn.class.getClassLoader(), "net.refination.refinecraft.spawn.Command", "refinecraft.", spawns);
    }

    @Override
    public void setSpawn(Location loc, String group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group");
        }
        spawns.setSpawn(loc, group);
    }

    @Override
    public Location getSpawn(String group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group");
        }
        return spawns.getSpawn(group);
    }
}
