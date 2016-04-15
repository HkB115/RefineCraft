package net.refination.refinecraft.commands;

import net.refination.refinecraft.Teleport;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandtpaccept extends RefineCraftCommand {
    public Commandtpaccept() {
        super("tpaccept");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final User requester;
        try {
            requester = RC.getUser(user.getTeleportRequest());
        } catch (Exception ex) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (!requester.getBase().isOnline()) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (user.isTpRequestHere() && ((!requester.isAuthorized("refinecraft.tpahere") && !requester.isAuthorized("refinecraft.tpaall")) || (user.getWorld() != requester.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + user.getWorld().getName())))) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (!user.isTpRequestHere() && (!requester.isAuthorized("refinecraft.tpa") || (user.getWorld() != requester.getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + requester.getWorld().getName())))) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (args.length > 0 && !requester.getName().contains(args[0])) {
            throw new Exception(tl("noPendingRequest"));
        }

        long timeout = RC.getSettings().getTpaAcceptCancellation();
        if (timeout != 0 && (System.currentTimeMillis() - user.getTeleportRequestTime()) / 1000 > timeout) {
            user.requestTeleport(null, false);
            throw new Exception(tl("requestTimedOut"));
        }

        final Trade charge = new Trade(this.getName(), RC);
        user.sendMessage(tl("requestAccepted"));
        requester.sendMessage(tl("requestAcceptedFrom", user.getDisplayName()));

        try {
            if (user.isTpRequestHere()) {
                final Location loc = user.getTpRequestLocation();
                Teleport teleport = requester.getTeleport();
                teleport.setTpType(Teleport.TeleportType.TPA);
                teleport.teleportPlayer(user, user.getTpRequestLocation(), charge, TeleportCause.COMMAND);
                requester.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            } else {
                Teleport teleport = requester.getTeleport();
                teleport.setTpType(Teleport.TeleportType.TPA);
                teleport.teleport(user.getBase(), charge, TeleportCause.COMMAND);
            }
        } catch (Exception ex) {
            user.sendMessage(tl("pendingTeleportCancelled"));
            RC.showError(requester.getSource(), ex, commandLabel);
        }
        user.requestTeleport(null, false);
        throw new NoChargeException();
    }

}
