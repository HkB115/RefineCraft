package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static net.refination.refinecraft.I18n.tl;


public class Commandme extends RefineCraftCommand {
    public Commandme() {
        super("me");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (user.isMuted()) {
            throw new Exception(tl("voiceSilenced"));
        }

        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.formatMessage(user, "refinecraft.chat", message);

        user.setDisplayNick();
        int radius = RC.getSettings().getChatRadius();
        String toSend = tl("action", user.getDisplayName(), message);
        if (radius < 1) {
            RC.broadcastMessage(user, toSend);
            return;
        }

        World world = user.getWorld();
        Location loc = user.getLocation();
        Set<Player> outList = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            final User onlineUser = RC.getUser(player);
            if (!onlineUser.equals(user)) {
                boolean abort = false;
                final Location playerLoc = onlineUser.getLocation();
                if (playerLoc.getWorld() != world) {
                    abort = true;
                } else {
                    final double delta = playerLoc.distanceSquared(loc);
                    if (delta > radius) {
                        abort = true;
                    }
                }
                if (abort) {
                    if (onlineUser.isAuthorized("refinecraft.chat.spy")) {
                        outList.add(player); // Just use the same list unless we wanted to format spyying for this.
                    }
                } else {
                    outList.add(player);
                }
            } else {
                outList.add(player); // Add yourself to the list.
            }
        }

        if (outList.size() < 2) {
            user.sendMessage(tl("localNoOne"));
        }

        for (Player onlinePlayer : outList) {
            onlinePlayer.sendMessage(toSend);
        }
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        message = FormatUtil.replaceFormat(message);

        RC.getServer().broadcastMessage(tl("action", "@", message));
    }
}
