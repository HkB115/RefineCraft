package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpall extends RefineCraftCommand {
    public Commandtpall() {
        super("tpall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            if (sender.isPlayer()) {
                teleportAllPlayers(server, sender, RC.getUser(sender.getPlayer()));
                return;
            }
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, sender, args, 0);
        teleportAllPlayers(server, sender, target);
    }

    private void teleportAllPlayers(Server server, CommandSource sender, User target) {
        sender.sendMessage(tl("teleportAll"));
        final Location loc = target.getLocation();
        for (User player : RC.getOnlineUsers()) {
            if (target == player) {
                continue;
            }
            if (sender.equals(target.getBase()) && target.getWorld() != player.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !target.isAuthorized("refinecraft.worlds." + target.getWorld().getName())) {
                continue;
            }
            try {
                player.getTeleport().now(loc, false, TeleportCause.COMMAND);
            } catch (Exception ex) {
                RC.showError(sender, ex, getName());
            }
        }
    }
}
