package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import net.refination.refinecraft.api.InterfaceWarps;
import net.refination.refinecraft.utils.NumberUtil;
import net.refination.refinecraft.utils.StringUtil;
import net.refination.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandsetwarp extends RefineCraftCommand {
    public Commandsetwarp() {
        super("setwarp");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        if (NumberUtil.isInt(args[0]) || args[0].isEmpty()) {
            throw new NoSuchFieldException(tl("invalidWarpName"));
        }

        final Location loc = user.getLocation();
        final InterfaceWarps warps = RC.getWarps();
        Location warpLoc = null;

        try {
            warpLoc = warps.getWarp(args[0]);
        } catch (WarpNotFoundException | InvalidWorldException ex) {
        }

        if (warpLoc == null || user.isAuthorized("refinecraft.warp.overwrite." + StringUtil.safeString(args[0]))) {
            warps.setWarp(args[0], loc);
        } else {
            throw new Exception(tl("warpOverwrite"));
        }
        user.sendMessage(tl("warpSet", args[0]));
    }
}
