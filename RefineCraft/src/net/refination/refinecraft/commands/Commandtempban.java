package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.DateUtil;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandtempban extends RefineCraftCommand {
    public Commandtempban() {
        super("tempban");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, true);
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.tempban.offline")) {
                sender.sendMessage(tl("tempbanExemptOffline"));
                return;
            }
        } else {
            if (user.isAuthorized("refinecraft.tempban.exempt") && sender.isPlayer()) {
                sender.sendMessage(tl("tempbanExempt"));
                return;
            }
        }
        final String time = getFinalArg(args, 1);
        final long banTimestamp = DateUtil.parseDateDiff(time, true);
        String banReason = DateUtil.removeTimePattern(time);

        final long maxBanLength = RC.getSettings().getMaxTempban() * 1000;
        if (maxBanLength > 0 && ((banTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxBanLength) && sender.isPlayer() && !(RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.tempban.unlimited"))) {
            sender.sendMessage(tl("oversizedTempban"));
            throw new NoChargeException();
        }

        if (banReason.length() < 2) {
            banReason = tl("defaultBanReason");
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        RC.getServer().getBanList(BanList.Type.NAME).addBan(user.getName(), banReason, new Date(banTimestamp), senderName);
        final String expiry = DateUtil.formatDateDiff(banTimestamp);

        final String banDisplay = tl("tempBanned", expiry, senderName, banReason);
        user.getBase().kickPlayer(banDisplay);

        final String message = tl("playerTempBanned", senderName, user.getName(), expiry, banReason);
        server.getLogger().log(Level.INFO, message);
        RC.broadcastMessage("refinecraft.ban.notify", message);
    }
}
