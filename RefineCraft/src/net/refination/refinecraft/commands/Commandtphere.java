package net.refination.refinecraft.commands;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandtphere extends RefineCraftCommand {
    public Commandtphere() {
        super("tphere");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final User player = getPlayer(server, user, args, 0);
        if (!player.isTeleportEnabled()) {
            throw new Exception(tl("teleportDisabled", player.getDisplayName()));
        }
        if (user.getWorld() != player.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + user.getWorld().getName())) {
            throw new Exception(tl("noPerm", "refinecraft.worlds." + user.getWorld().getName()));
        }
        user.getTeleport().teleportPlayer(player, user.getBase(), new Trade(this.getName(), RC), TeleportCause.COMMAND);
        throw new NoChargeException();
    }
}
