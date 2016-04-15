package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpo extends RefineCraftCommand {
    public Commandtpo() {
        super("tpo");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        switch (args.length) {
            case 0:
                throw new NotEnoughArgumentsException();

            case 1:
                final User player = getPlayer(server, user, args, 0);
                if (user.getWorld() != player.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + player.getWorld().getName())) {
                    throw new Exception(tl("noPerm", "refinecraft.worlds." + player.getWorld().getName()));
                }
                user.getTeleport().now(player.getBase(), false, TeleportCause.COMMAND);
                break;

            default:
                if (!user.isAuthorized("refinecraft.tp.others")) {
                    throw new Exception(tl("noPerm", "refinecraft.tp.others"));
                }
                final User target = getPlayer(server, user, args, 0);
                final User toPlayer = getPlayer(server, user, args, 1);

                if (target.getWorld() != toPlayer.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + toPlayer.getWorld().getName())) {
                    throw new Exception(tl("noPerm", "refinecraft.worlds." + toPlayer.getWorld().getName()));
                }

                target.getTeleport().now(toPlayer.getBase(), false, TeleportCause.COMMAND);
                target.sendMessage(tl("teleportAtoB", user.getDisplayName(), toPlayer.getDisplayName()));
                break;
        }
    }
}
