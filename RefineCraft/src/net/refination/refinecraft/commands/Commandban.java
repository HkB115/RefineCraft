package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.OfflinePlayer;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandban extends RefineCraftCommand {
    public Commandban() {
        super("ban");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean nomatch = false;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User user;
        try {
            user = getPlayer(server, args, 0, true, true);
        } catch (PlayerNotFoundException e) {
            nomatch = true;
            user = RC.getUser(new OfflinePlayer(args[0], RC.getServer()));
        }
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.ban.offline")) {
                throw new Exception(tl("banExemptOffline"));
            }
        } else {
            if (user.isAuthorized("refinecraft.ban.exempt") && sender.isPlayer()) {
                throw new Exception(tl("banExempt"));
            }
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        String banReason;
        if (args.length > 1) {
            banReason = FormatUtil.replaceFormat(getFinalArg(args, 1).replace("\\n", "\n").replace("|", "\n"));
        } else {
            banReason = tl("defaultBanReason");
        }

        RC.getServer().getBanList(BanList.Type.NAME).addBan(user.getName(), banReason, null, senderName);

        String banDisplay = tl("banFormat", banReason, senderName);

        user.getBase().kickPlayer(banDisplay);
        server.getLogger().log(Level.INFO, tl("playerBanned", senderName, user.getName(), banDisplay));

        if (nomatch) {
            sender.sendMessage(tl("userUnknown", user.getName()));
        }

        RC.broadcastMessage("refinecraft.ban.notify", tl("playerBanned", senderName, user.getName(), banReason));
    }
}
