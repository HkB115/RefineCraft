package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpohere extends RefineCraftCommand {
    public Commandtpohere() {
        super("tpohere");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        //Just basically the old tphere command
        final User player = getPlayer(server, user, args, 0);

        if (user.getWorld() != player.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + user.getWorld().getName())) {
            throw new Exception(tl("noPerm", "refinecraft.worlds." + user.getWorld().getName()));
        }

        // Verify permission
        player.getTeleport().now(user.getBase(), false, TeleportCause.COMMAND);
    }
}
