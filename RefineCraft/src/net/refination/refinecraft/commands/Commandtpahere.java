package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpahere extends RefineCraftCommand {
    public Commandtpahere() {
        super("tpahere");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User player = getPlayer(server, user, args, 0);
        if (user.getName().equalsIgnoreCase(player.getName())) {
            throw new NotEnoughArgumentsException();
        }
        if (!player.isTeleportEnabled()) {
            throw new Exception(tl("teleportDisabled", player.getDisplayName()));
        }
        if (user.getWorld() != player.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + user.getWorld().getName())) {
            throw new Exception(tl("noPerm", "refinecraft.worlds." + user.getWorld().getName()));
        }
        if (!player.isIgnoredPlayer(user)) {
            player.requestTeleport(user, true);
            player.sendMessage(tl("teleportHereRequest", user.getDisplayName()));
            player.sendMessage(tl("typeTpaccept"));
            player.sendMessage(tl("typeTpdeny"));
            if (RC.getSettings().getTpaAcceptCancellation() != 0) {
                player.sendMessage(tl("teleportRequestTimeoutInfo", RC.getSettings().getTpaAcceptCancellation()));
            }
        }
        user.sendMessage(tl("requestSent", player.getDisplayName()));
    }
}
