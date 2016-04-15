package net.refination.refinecraft.api;

import net.refination.refinecraft.Trade;
import net.refination.api.InterfaceUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;


public interface InterfaceTeleport {
    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param loc      - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void now(Location loc, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param entity   - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void now(Player entity, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    @Deprecated
    void teleport(Location loc, Trade chargeFor) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void teleport(Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void teleport(Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param otherUser - Which user will be teleported
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void teleportPlayer(InterfaceUser otherUser, Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param otherUser - Which user will be teleported
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void teleportPlayer(InterfaceUser otherUser, Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle tp fallback on /jail and /home
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void respawn(final Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /warp teleports
     *
     * @param otherUser - Which user will be teleported
     * @param warp      - The name of the warp the user will be teleported too.
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    void warp(InterfaceUser otherUser, String warp, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     *
     * @throws Exception
     */
    void back(Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle throwing user home after a jail sentence
     *
     * @throws Exception
     */
    void back() throws Exception;

}