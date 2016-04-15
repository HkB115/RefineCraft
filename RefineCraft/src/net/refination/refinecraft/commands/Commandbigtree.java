package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;

import static net.refination.refinecraft.I18n.tl;


public class Commandbigtree extends RefineCraftCommand {
    public Commandbigtree() {
        super("bigtree");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        TreeType tree;
        if (args.length > 0 && args[0].equalsIgnoreCase("redwood")) {
            tree = TreeType.TALL_REDWOOD;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("tree")) {
            tree = TreeType.BIG_TREE;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("jungle")) {
            tree = TreeType.JUNGLE;
        } else {
            throw new NotEnoughArgumentsException();
        }

        final Location loc = LocationUtil.getTarget(user.getBase());
        final Location safeLocation = LocationUtil.getSafeDestination(loc);
        final boolean success = user.getWorld().generateTree(safeLocation, tree);
        if (success) {
            user.sendMessage(tl("bigTreeSuccess"));
        } else {
            throw new Exception(tl("bigTreeFailure"));
        }
    }
}
