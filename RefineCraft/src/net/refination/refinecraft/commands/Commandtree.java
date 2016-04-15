package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;

import static net.refination.refinecraft.I18n.tl;

public class Commandtree extends RefineCraftCommand {
    public Commandtree() {
        super("tree");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        TreeType tree = null;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        } else {
            for (TreeType type : TreeType.values()) {
                if (type.name().replace("_", "").equalsIgnoreCase(args[0])) {
                    tree = type;
                    break;
                }
            }
            if (args[0].equalsIgnoreCase("jungle")) {
                tree = TreeType.SMALL_JUNGLE;
            }
            if (tree == null) {
                throw new NotEnoughArgumentsException();
            }
        }

        final Location loc = LocationUtil.getTarget(user.getBase());
        final Location safeLocation = LocationUtil.getSafeDestination(loc);
        final boolean success = user.getWorld().generateTree(safeLocation, tree);
        if (success) {
            user.sendMessage(tl("treeSpawned"));
        } else {
            user.sendMessage(tl("treeFailure"));
        }
    }
}
