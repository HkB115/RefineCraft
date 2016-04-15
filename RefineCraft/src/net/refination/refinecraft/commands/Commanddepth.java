package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commanddepth extends RefineCraftCommand {
    public Commanddepth() {
        super("depth");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int depth = user.getLocation().getBlockY() - 63;
        if (depth > 0) {
            user.sendMessage(tl("depthAboveSea", depth));
        } else if (depth < 0) {
            user.sendMessage(tl("depthBelowSea", (-depth)));
        } else {
            user.sendMessage(tl("depth"));
        }
    }
}
