package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.TNTPrimed;

// This command has a in theme message that only shows if you supply a parameter #EasterEgg
public class Commandantioch extends RefineCraftCommand {
    public Commandantioch() {
        super("antioch");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length > 0) {
            RC.broadcastMessage(user, "...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,");
            RC.broadcastMessage(user, "who being naughty in My sight, shall snuff it.");
        }

        final Location loc = LocationUtil.getTarget(user.getBase());
        loc.getWorld().spawn(loc, TNTPrimed.class);
    }
}
