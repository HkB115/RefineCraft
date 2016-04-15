package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.OfflinePlayer;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.DateUtil;
import org.bukkit.Server;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandmute extends RefineCraftCommand {
    public Commandmute() {
        super("mute");
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
            if (sender.isPlayer() && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.mute.offline")) {
                throw new Exception(tl("muteExemptOffline"));
            }
        } else {
            if (user.isAuthorized("refinecraft.mute.exempt") && sender.isPlayer()) {
                throw new Exception(tl("muteExempt"));
            }
        }

        long muteTimestamp = 0;

        if (args.length > 1) {
            final String time = getFinalArg(args, 1);
            muteTimestamp = DateUtil.parseDateDiff(time, true);
            user.setMuted(true);
        } else {
            user.setMuted(!user.getMuted());
        }
        user.setMuteTimeout(muteTimestamp);
        final boolean muted = user.getMuted();
        String muteTime = DateUtil.formatDateDiff(muteTimestamp);

        if (nomatch) {
            sender.sendMessage(tl("userUnknown", user.getName()));
        }

        if (muted) {
            if (muteTimestamp > 0) {
                sender.sendMessage(tl("mutedPlayerFor", user.getDisplayName(), muteTime));
                user.sendMessage(tl("playerMutedFor", muteTime));
            } else {
                sender.sendMessage(tl("mutedPlayer", user.getDisplayName()));
                user.sendMessage(tl("playerMuted"));
            }
            final String message = tl("muteNotify", sender.getSender().getName(), user.getName(), muteTime);
            server.getLogger().log(Level.INFO, message);
            RC.broadcastMessage("refinecraft.mute.notify", message);
        } else {
            sender.sendMessage(tl("unmutedPlayer", user.getDisplayName()));
            user.sendMessage(tl("playerUnmuted"));
        }
    }
}
