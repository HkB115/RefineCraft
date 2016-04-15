package net.refination.refinecraft.commands;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;
import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandhome extends RefineCraftCommand {
    public Commandhome() {
        super("home");
    }

    // This method contains an undocumented translation parameters #EasterEgg
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), RC);
        User player = user;
        String homeName = "";
        String[] nameParts;
        if (args.length > 0) {
            nameParts = args[0].split(":");
            if (nameParts[0].length() == args[0].length() || !user.isAuthorized("refinecraft.home.others")) {
                homeName = nameParts[0];
            } else {
                player = getPlayer(server, nameParts, 0, true, true);
                if (nameParts.length > 1) {
                    homeName = nameParts[1];
                }
            }
        }
        try {
            if ("bed".equalsIgnoreCase(homeName) && user.isAuthorized("refinecraft.home.bed")) {
                final Location bed = player.getBase().getBedSpawnLocation();
                if (bed != null) {
                    user.getTeleport().teleport(bed, charge, TeleportCause.COMMAND);
                    throw new NoChargeException();
                } else {
                    throw new Exception(tl("bedMissing"));
                }
            }
            goHome(user, player, homeName.toLowerCase(Locale.ENGLISH), charge);
        } catch (NotEnoughArgumentsException e) {
            Location bed = player.getBase().getBedSpawnLocation();
            final List<String> homes = player.getHomes();
            if (homes.isEmpty() && player.equals(user)) {
                user.getTeleport().respawn(charge, TeleportCause.COMMAND);
            } else if (homes.isEmpty()) {
                throw new Exception(tl("noHomeSetPlayer"));
            } else if (homes.size() == 1 && player.equals(user)) {
                goHome(user, player, homes.get(0), charge);
            } else {
                final int count = homes.size();
                if (user.isAuthorized("refinecraft.home.bed")) {
                    if (bed != null) {
                        homes.add(tl("bed"));
                    } else {
                        homes.add(tl("bedNull"));
                    }
                }
                user.sendMessage(tl("homes", StringUtil.joinList(homes), count, getHomeLimit(player)));
            }
        }
        throw new NoChargeException();
    }

    private String getHomeLimit(final User player) {
        if (!player.getBase().isOnline()) {
            return "?";
        }
        if (player.isAuthorized("refinecraft.sethome.multiple.unlimited")) {
            return "*";
        }
        return Integer.toString(RC.getSettings().getHomeLimit(player));
    }

    private void goHome(final User user, final User player, final String home, final Trade charge) throws Exception {
        if (home.length() < 1) {
            throw new NotEnoughArgumentsException();
        }
        final Location loc = player.getHome(home);
        if (loc == null) {
            throw new NotEnoughArgumentsException();
        }
        if (user.getWorld() != loc.getWorld() && RC.getSettings().isWorldHomePermissions() && !user.isAuthorized("refinecraft.worlds." + loc.getWorld().getName())) {
            throw new Exception(tl("noPerm", "refinecraft.worlds." + loc.getWorld().getName()));
        }
        user.getTeleport().teleport(loc, charge, TeleportCause.COMMAND);
    }
}
