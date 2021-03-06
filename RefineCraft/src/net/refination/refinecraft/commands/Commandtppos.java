package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FloatUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class Commandtppos extends RefineCraftCommand {
    public Commandtppos() {
        super("tppos");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 3) {
            throw new NotEnoughArgumentsException();
        }

        final double x = args[0].startsWith("~") ? user.getLocation().getX() + (args[0].length() > 1 ? Integer.parseInt(args[0].substring(1)) : 0) : Integer.parseInt(args[0]);
        final double y = args[1].startsWith("~") ? user.getLocation().getY() + (args[1].length() > 1 ? Integer.parseInt(args[1].substring(1)) : 0) : Integer.parseInt(args[1]);
        final double z = args[2].startsWith("~") ? user.getLocation().getZ() + (args[2].length() > 1 ? Integer.parseInt(args[2].substring(1)) : 0) : Integer.parseInt(args[2]);
        final Location loc = new Location(user.getWorld(), x, y, z, user.getLocation().getYaw(), user.getLocation().getPitch());
        if (args.length == 4) {
            loc.setWorld(RC.getWorld(args[3]));
        }
        if (args.length > 4) {
            loc.setYaw((FloatUtil.parseFloat(args[3]) + 360) % 360);
            loc.setPitch(FloatUtil.parseFloat(args[4]));
        }
        if (args.length > 5) {
            loc.setWorld(RC.getWorld(args[5]));
        }
        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000) {
            throw new NotEnoughArgumentsException(tl("teleportInvalidLocation"));
        }
        final Trade charge = new Trade(this.getName(), RC);
        charge.isAffordableFor(user);
        user.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.getTeleport().teleport(loc, charge, TeleportCause.COMMAND);
        throw new NoChargeException();
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 4) {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, args, 0, true, false);
        final double x = args[1].startsWith("~") ? user.getLocation().getX() + (args[1].length() > 1 ? Integer.parseInt(args[1].substring(1)) : 0) : Integer.parseInt(args[1]);
        final double y = args[2].startsWith("~") ? user.getLocation().getY() + (args[2].length() > 1 ? Integer.parseInt(args[2].substring(1)) : 0) : Integer.parseInt(args[2]);
        final double z = args[3].startsWith("~") ? user.getLocation().getZ() + (args[3].length() > 1 ? Integer.parseInt(args[3].substring(1)) : 0) : Integer.parseInt(args[3]);
        final Location loc = new Location(user.getWorld(), x, y, z, user.getLocation().getYaw(), user.getLocation().getPitch());
        if (args.length == 5) {
            loc.setWorld(RC.getWorld(args[4]));
        }
        if (args.length > 5) {
            loc.setYaw((FloatUtil.parseFloat(args[4]) + 360) % 360);
            loc.setPitch(FloatUtil.parseFloat(args[5]));
        }
        if (args.length > 6) {
            loc.setWorld(RC.getWorld(args[6]));
        }
        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000) {
            throw new NotEnoughArgumentsException(tl("teleportInvalidLocation"));
        }
        sender.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.getTeleport().teleport(loc, null, TeleportCause.COMMAND);

    }
}
