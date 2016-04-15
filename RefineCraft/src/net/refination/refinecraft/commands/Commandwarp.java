package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.api.InterfaceWarps;
import net.refination.refinecraft.utils.NumberUtil;
import net.refination.refinecraft.utils.StringUtil;
import net.refination.api.InterfaceUser;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandwarp extends RefineCraftCommand {
    private static final int WARPS_PER_PAGE = 20;

    public Commandwarp() {
        super("warp");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || args[0].matches("[0-9]+")) {
            if (!user.isAuthorized("refinecraft.warp.list")) {
                throw new Exception(tl("warpListPermission"));
            }
            warpList(user.getSource(), args, user);
            throw new NoChargeException();
        }
        if (args.length > 0) {
            //TODO: Remove 'otherplayers' permission.
            User otherUser = null;
            if (args.length == 2 && (user.isAuthorized("refinecraft.warp.otherplayers") || user.isAuthorized("refinecraft.warp.others"))) {
                otherUser = getPlayer(server, user, args, 1);
                warpUser(user, otherUser, args[0]);
                throw new NoChargeException();
            }
            warpUser(user, user, args[0]);
            throw new NoChargeException();
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2 || NumberUtil.isInt(args[0])) {
            warpList(sender, args, null);
            throw new NoChargeException();
        }
        User otherUser = getPlayer(server, args, 1, true, false);
        otherUser.getTeleport().warp(otherUser, args[0], null, TeleportCause.COMMAND);
        throw new NoChargeException();

    }

    //TODO: Use one of the new text classes, like /help ?
    private void warpList(final CommandSource sender, final String[] args, final InterfaceUser user) throws Exception {
        final InterfaceWarps warps = RC.getWarps();
        final List<String> warpNameList = new ArrayList<>(warps.getList());

        if (user != null) {
            final Iterator<String> iterator = warpNameList.iterator();
            while (iterator.hasNext()) {
                final String warpName = iterator.next();
                if (RC.getSettings().getPerWarpPermission() && !user.isAuthorized("refinecraft.warps." + warpName)) {
                    iterator.remove();
                }
            }
        }
        if (warpNameList.isEmpty()) {
            throw new Exception(tl("noWarpsDefined"));
        }
        int page = 1;
        if (args.length > 0 && NumberUtil.isInt(args[0])) {
            page = Integer.parseInt(args[0]);
        }

        final int maxPages = (int) Math.ceil(warpNameList.size() / (double) WARPS_PER_PAGE);

        if (page > maxPages) {
            page = maxPages;
        }

        final int warpPage = (page - 1) * WARPS_PER_PAGE;
        final String warpList = StringUtil.joinList(warpNameList.subList(warpPage, warpPage + Math.min(warpNameList.size() - warpPage, WARPS_PER_PAGE)));

        if (warpNameList.size() > WARPS_PER_PAGE) {
            sender.sendMessage(tl("warpsCount", warpNameList.size(), page, maxPages));
            sender.sendMessage(tl("warpList", warpList));
        } else {
            sender.sendMessage(tl("warps", warpList));
        }
    }

    private void warpUser(final User owner, final User user, final String name) throws Exception {
        final Trade chargeWarp = new Trade("warp-" + name.toLowerCase(Locale.ENGLISH).replace('_', '-'), RC);
        final Trade chargeCmd = new Trade(this.getName(), RC);
        final BigDecimal fullCharge = chargeWarp.getCommandCost(user).add(chargeCmd.getCommandCost(user));
        final Trade charge = new Trade(fullCharge, RC);
        charge.isAffordableFor(owner);
        if (RC.getSettings().getPerWarpPermission() && !owner.isAuthorized("refinecraft.warps." + name)) {
            throw new Exception(tl("warpUsePermission"));
        }
        owner.getTeleport().warp(user, name, charge, TeleportCause.COMMAND);
    }
}
