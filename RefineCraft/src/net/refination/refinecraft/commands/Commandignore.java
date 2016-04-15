package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandignore extends RefineCraftCommand {
    public Commandignore() {
        super("ignore");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : user._getIgnoredPlayers()) {
                sb.append(s).append(" ");
            }
            String ignoredList = sb.toString().trim();
            user.sendMessage(ignoredList.length() > 0 ? tl("ignoredList", ignoredList) : tl("noIgnored"));
        } else {
            User player;
            try {
                player = getPlayer(server, args, 0, true, true);
            } catch (PlayerNotFoundException ex) {
                player = RC.getOfflineUser(args[0]);
            }
            if (player == null) {
                throw new PlayerNotFoundException();
            }
            if (player.isIgnoreExempt()) {
                user.sendMessage(tl("ignoreExempt"));
            } else if (user.isIgnoredPlayer(player)) {
                user.setIgnoredPlayer(player, false);
                user.sendMessage(tl("unignorePlayer", player.getName()));
            } else {
                user.setIgnoredPlayer(player, true);
                user.sendMessage(tl("ignorePlayer", player.getName()));
            }
        }
    }
}
