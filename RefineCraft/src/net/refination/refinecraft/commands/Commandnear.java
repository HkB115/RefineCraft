package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import static net.refination.refinecraft.I18n.tl;


public class Commandnear extends RefineCraftCommand {
    public Commandnear() {
        super("near");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        long maxRadius = RC.getSettings().getNearRadius();

        if (maxRadius == 0) {
            maxRadius = 200;
        }

        long radius = maxRadius;

        User otherUser = null;

        if (args.length > 0) {
            try {
                radius = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                try {
                    otherUser = getPlayer(server, user, args, 0);
                } catch (Exception ex) {
                }
            }
            if (args.length > 1 && otherUser != null) {
                try {
                    radius = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                }
            }
        }

        radius = Math.abs(radius);

        if (radius > maxRadius && !user.isAuthorized("refinecraft.near.maxexempt")) {
            user.sendMessage(tl("radiusTooBig", maxRadius));
            radius = maxRadius;
        }

        if (otherUser == null || !user.isAuthorized("refinecraft.near.others")) {
            otherUser = user;
        }
        user.sendMessage(tl("nearbyPlayers", getLocal(server, otherUser, radius)));
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final User otherUser = getPlayer(server, args, 0, true, false);
        long radius = 200;
        if (args.length > 1) {
            try {
                radius = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
            }
        }
        sender.sendMessage(tl("nearbyPlayers", getLocal(server, otherUser, radius)));
    }

    private String getLocal(final Server server, final User user, final long radius) {
        final Location loc = user.getLocation();
        final World world = loc.getWorld();
        final StringBuilder output = new StringBuilder();
        final long radiusSquared = radius * radius;
        boolean showHidden = user.canInteractGhosted();

        for (User player : RC.getOnlineUsers()) {
            if (!player.equals(user) && (!player.isHidden(user.getBase()) || showHidden || user.getBase().canSee(player.getBase()))) {
                final Location playerLoc = player.getLocation();
                if (playerLoc.getWorld() != world) {
                    continue;
                }

                final long delta = (long) playerLoc.distanceSquared(loc);
                if (delta < radiusSquared) {
                    if (output.length() > 0) {
                        output.append(", ");
                    }
                    output.append(player.getDisplayName()).append("§f(§4").append((long) Math.sqrt(delta)).append("m§f)");
                }
            }
        }
        return output.length() > 1 ? output.toString() : tl("none");
    }
}
