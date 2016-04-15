package net.refination.refinecraft;

import net.refination.refinecraft.commands.InterfaceRefineCraftCommand;
import net.refination.api.InterfaceTeleport;
import net.refination.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InterfaceUser {
    boolean isAuthorized(String node);

    boolean isAuthorized(InterfaceRefineCraftCommand cmd);

    boolean isAuthorized(InterfaceRefineCraftCommand cmd, String permissionPrefix);

    void healCooldown() throws Exception;

    void giveMoney(BigDecimal value) throws MaxMoneyException;

    void giveMoney(final BigDecimal value, final CommandSource initiator) throws MaxMoneyException;

    void payUser(final User reciever, final BigDecimal value) throws Exception;

    void takeMoney(BigDecimal value);

    void takeMoney(final BigDecimal value, final CommandSource initiator);

    boolean canAfford(BigDecimal value);

    Boolean canSpawnItem(final int itemId);

    void setLastLocation();

    void setLogoutLocation();

    void requestTeleport(final User player, final boolean here);

    InterfaceTeleport getTeleport();

    BigDecimal getMoney();

    void setMoney(final BigDecimal value) throws MaxMoneyException;

    void setAfk(final boolean set);

    /**
     * 'Hidden' Represents when a player is hidden from others. This status includes when the player is hidden via other
     * supported plugins. Use isGhost() if you want to check if a user is ghosted by RefineCraft.
     *
     * @return If the user is hidden or not
     *
     * @see isGhost
     */
    boolean isHidden();

    void setHidden(boolean ghost);

    boolean isGodModeEnabled();

    String getGroup();

    boolean inGroup(final String group);

    boolean canBuild();

    long getTeleportRequestTime();

    void enableInvulnerabilityAfterTeleport();

    void resetInvulnerabilityAfterTeleport();

    boolean hasInvulnerabilityAfterTeleport();

    /**
     * 'Ghosted' Represents when a player is hidden from others by RefineCraft. This status does NOT include when the
     * player is hidden via other plugins. Use isHidden() if you want to check if a user is ghosted by any supported
     * plugin.
     *
     * @return If the user is ghosted or not
     *
     * @see isHidden
     */
    boolean isGhost();

    void setGhosted(boolean ghost);

    boolean isIgnoreExempt();

    void sendMessage(String message);

    /*
     * UserData
     */
    Location getHome(String name) throws Exception;

    Location getHome(Location loc) throws Exception;

    List<String> getHomes();

    void setHome(String name, Location loc);

    void delHome(String name) throws Exception;

    boolean hasHome();

    Location getLastLocation();

    Location getLogoutLocation();

    long getLastTeleportTimestamp();

    void setLastTeleportTimestamp(long time);

    String getJail();

    void setJail(String jail);

    List<String> getMails();

    void addMail(String mail);

    boolean isAfk();

    void setIgnoreMsg(boolean ignoreMsg);

    boolean isIgnoreMsg();

    void setConfigProperty(String node, Object object);

    Set<String> getConfigKeys();

    Map<String, Object> getConfigMap();

    Map<String, Object> getConfigMap(String node);

    /*
     *  PlayerExtension
     */
    Player getBase();

    CommandSource getSource();

    String getName();
}
