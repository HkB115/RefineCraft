package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.LocationUtil;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandsethome extends RefineCraftCommand {
    public Commandsethome() {
        super("sethome");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, String[] args) throws Exception {
        User usersHome = user;
        String name = "home";
        final Location location = user.getLocation();

        if (args.length > 0) {
            //Allowing both formats /sethome khobbits house | /sethome khobbits:house
            final String[] nameParts = args[0].split(":");
            if (nameParts[0].length() != args[0].length()) {
                args = nameParts;
            }

            if (args.length < 2) {
                name = args[0].toLowerCase(Locale.ENGLISH);
            } else {
                name = args[1].toLowerCase(Locale.ENGLISH);
                if (user.isAuthorized("refinecraft.sethome.others")) {
                    usersHome = getPlayer(server, args[0], true, true);
                    if (usersHome == null) {
                        throw new PlayerNotFoundException();
                    }
                }
            }
        }
        if (checkHomeLimit(user, usersHome, name)) {
            name = "home";
        }
        if ("bed".equals(name) || NumberUtil.isInt(name)) {
            throw new NoSuchFieldException(tl("invalidHomeName"));
        }

        if (!RC.getSettings().isTeleportSafetyEnabled() && LocationUtil.isBlockUnsafeForUser(usersHome, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
            throw new Exception(tl("unsafeTeleportDestination", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }

        usersHome.setHome(name, location);
        user.sendMessage(tl("homeSet", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ(), name));

    }

    private boolean checkHomeLimit(final User user, final User usersHome, String name) throws Exception {
        if (!user.isAuthorized("refinecraft.sethome.multiple.unlimited")) {
            int limit = RC.getSettings().getHomeLimit(user);
            if (usersHome.getHomes().size() == limit && usersHome.getHomes().contains(name)) {
                return false;
            }
            if (usersHome.getHomes().size() >= limit) {
                throw new Exception(tl("maxHomes", RC.getSettings().getHomeLimit(user)));
            }
            if (limit == 1) {
                return true;
            }
        }
        return false;
    }
}
