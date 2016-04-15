package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import org.bukkit.BanList;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandunban extends RefineCraftCommand {
    public Commandunban() {
        super("unban");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        String name;
        try {
            final User user = getPlayer(server, args, 0, true, true);
            name = user.getName();
            RC.getServer().getBanList(BanList.Type.NAME).pardon(name);
        } catch (NoSuchFieldException e) {
            final OfflinePlayer player = server.getOfflinePlayer(args[0]);
            name = player.getName();
            if (!player.isBanned()) {
                throw new Exception(tl("playerNotFound"), e);
            }
            RC.getServer().getBanList(BanList.Type.NAME).pardon(name);
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        server.getLogger().log(Level.INFO, tl("playerUnbanned", senderName, name));

        RC.broadcastMessage("refinecraft.ban.notify", tl("playerUnbanned", senderName, name));
    }
}
