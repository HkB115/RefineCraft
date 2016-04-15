package net.refination.refinecraft.spawn;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.commands.RefineCraftCommand;
import net.refination.refinecraft.commands.NoChargeException;
import net.refination.refinecraft.commands.NotEnoughArgumentsException;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandspawn extends RefineCraftCommand {
    public Commandspawn() {
        super("spawn");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), RC);
        charge.isAffordableFor(user);
        if (args.length > 0 && user.isAuthorized("refinecraft.spawn.others")) {
            final User otherUser = getPlayer(server, user, args, 0);
            respawn(user.getSource(), user, otherUser, charge);
            if (!otherUser.equals(user)) {
                otherUser.sendMessage(tl("teleportAtoB", user.getDisplayName(), "spawn"));
            }
        } else {
            respawn(user.getSource(), user, user, charge);
        }
        throw new NoChargeException();
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, false);
        respawn(sender, null, user, null);
        user.sendMessage(tl("teleportAtoB", Console.NAME, "spawn"));

    }

    private void respawn(final CommandSource sender, final User teleportOwner, final User teleportee, final Trade charge) throws Exception {
        final SpawnStorage spawns = (SpawnStorage) this.module;
        final Location spawn = spawns.getSpawn(teleportee.getGroup());
        sender.sendMessage(tl("teleporting", spawn.getWorld().getName(), spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
        if (teleportOwner == null) {
            teleportee.getTeleport().now(spawn, false, TeleportCause.COMMAND);
        } else {
            teleportOwner.getTeleport().teleportPlayer(teleportee, spawn, charge, TeleportCause.COMMAND);
        }
    }
}
