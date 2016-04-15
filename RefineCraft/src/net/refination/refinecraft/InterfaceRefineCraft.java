package net.refination.refinecraft;

import net.refination.refinecraft.api.InterfaceItemDb;
import net.refination.refinecraft.api.InterfaceJails;
import net.refination.refinecraft.api.InterfaceWarps;
import net.refination.refinecraft.metrics.Metrics;
import net.refination.refinecraft.perm.PermissionsHandler;
import net.refination.refinecraft.register.payment.Methods;
import net.refination.nms.SpawnerProvider;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


public interface InterfaceRefineCraft extends Plugin {
    void addReloadListener(InterfaceConf listener);

    void reload();

    boolean onCommandRefineCraft(CommandSender sender, Command command, String commandLabel, String[] args, ClassLoader classLoader, String commandPath, String permissionPrefix, InterfaceRefineCraftModule module);

    @Deprecated
    User getUser(Object base);

    User getUser(UUID base);

    User getUser(String base);

    User getUser(Player base);

    I18n getI18n();

    User getOfflineUser(String name);

    World getWorld(String name);

    int broadcastMessage(String message);

    int broadcastMessage(InterfaceUser sender, String message);

    int broadcastMessage(String permission, String message);

    InterfaceSettings getSettings();

    BukkitScheduler getScheduler();

    InterfaceJails getJails();

    InterfaceWarps getWarps();

    Worth getWorth();

    Backup getBackup();

    Methods getPaymentMethod();

    BukkitTask runTaskAsynchronously(Runnable run);

    BukkitTask runTaskLaterAsynchronously(Runnable run, long delay);

    BukkitTask runTaskTimerAsynchronously(Runnable run, long delay, long period);

    int scheduleSyncDelayedTask(Runnable run);

    int scheduleSyncDelayedTask(Runnable run, long delay);

    int scheduleSyncRepeatingTask(Runnable run, long delay, long period);

    TNTExplodeListener getTNTListener();

    PermissionsHandler getPermissionsHandler();

    AlternativeCommandsHandler getAlternativeCommandsHandler();

    void showError(CommandSource sender, Throwable exception, String commandLabel);

    InterfaceItemDb getItemDb();

    UserMap getUserMap();

    Metrics getMetrics();

    void setMetrics(Metrics metrics);

    RefineCraftTimer getTimer();

    List<String> getGhostedPlayers();

    Collection<Player> getOnlinePlayers();

    Iterable<User> getOnlineUsers();

    SpawnerProvider getSpawnerProvider();
}
