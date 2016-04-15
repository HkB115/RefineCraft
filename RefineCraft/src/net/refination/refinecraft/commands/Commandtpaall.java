package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpaall extends RefineCraftCommand {
    public Commandtpaall() {
        super("tpaall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            if (sender.isPlayer()) {
                teleportAAllPlayers(server, sender, RC.getUser(sender.getPlayer()));
                return;
            }
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, sender, args, 0);
        teleportAAllPlayers(server, sender, target);
    }

    private void teleportAAllPlayers(final Server server, final CommandSource sender, final User target) {
        sender.sendMessage(tl("teleportAAll"));
        for (User player : RC.getOnlineUsers()) {
            if (target == player) {
                continue;
            }
            if (!player.isTeleportEnabled()) {
                continue;
            }
            if (sender.equals(target.getBase()) && target.getWorld() != player.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !target.isAuthorized("refinecraft.worlds." + target.getWorld().getName())) {
                continue;
            }
            try {
                player.requestTeleport(target, true);
                player.sendMessage(tl("teleportHereRequest", target.getDisplayName()));
                player.sendMessage(tl("typeTpaccept"));
                if (RC.getSettings().getTpaAcceptCancellation() != 0) {
                    player.sendMessage(tl("teleportRequestTimeoutInfo", RC.getSettings().getTpaAcceptCancellation()));
                }
            } catch (Exception ex) {
                RC.showError(sender, ex, getName());
            }
        }
    }
}
