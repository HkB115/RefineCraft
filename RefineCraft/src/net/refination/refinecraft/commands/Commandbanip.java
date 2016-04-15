package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


//TODO: Add kick to online players matching ip ban.
public class Commandbanip extends RefineCraftCommand {
    public Commandbanip() {
        super("banip");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;

        String ipAddress;
        if (FormatUtil.validIP(args[0])) {
            ipAddress = args[0];
        } else {
            try {
                User player = getPlayer(server, args, 0, true, true);
                ipAddress = player.getLastLoginAddress();
            } catch (PlayerNotFoundException ex) {
                ipAddress = args[0];
            }
        }

        if (ipAddress.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        String banReason;
        if (args.length > 1) {
            banReason = FormatUtil.replaceFormat(getFinalArg(args, 1).replace("\\n", "\n").replace("|", "\n"));
        } else {
            banReason = tl("defaultBanReason");
        }

        RC.getServer().getBanList(BanList.Type.IP).addBan(ipAddress, banReason, null, senderName);
        server.getLogger().log(Level.INFO, tl("playerBanIpAddress", senderName, ipAddress, banReason));

        RC.broadcastMessage("refinecraft.banip.notify", tl("playerBanIpAddress", senderName, ipAddress, banReason));
    }
}
