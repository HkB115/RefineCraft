package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpdeny extends RefineCraftCommand {
    public Commandtpdeny() {
        super("tpdeny");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final User player = RC.getUser(user.getTeleportRequest());
        if (player == null) {
            throw new Exception(tl("noPendingRequest"));
        }

        user.sendMessage(tl("requestDenied"));
        player.sendMessage(tl("requestDeniedFrom", user.getDisplayName()));
        user.requestTeleport(null, false);
    }
}
