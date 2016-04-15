package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandpowertooltoggle extends RefineCraftCommand {
    public Commandpowertooltoggle() {
        super("powertooltoggle");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (!user.hasPowerTools()) {
            user.sendMessage(tl("noPowerTools"));
            return;
        }
        user.sendMessage(user.togglePowerToolsEnabled() ? tl("powerToolsEnabled") : tl("powerToolsDisabled"));
    }
}
