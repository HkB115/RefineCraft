package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandfly extends RefineCraftToggleCommand {
    public Commandfly() {
        super("fly", "refinecraft.fly.others");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        handleToggleWithArgs(server, user, args);
    }

    @Override
    void togglePlayer(CommandSource sender, User user, Boolean enabled) {
        if (enabled == null) {
            enabled = !user.getBase().getAllowFlight();
        }

        user.getBase().setFallDistance(0f);
        user.getBase().setAllowFlight(enabled);

        if (!user.getBase().getAllowFlight()) {
            user.getBase().setFlying(false);
        }

        user.sendMessage(tl("flyMode", tl(enabled ? "enabled" : "disabled"), user.getDisplayName()));
        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendMessage(tl("flyMode", tl(enabled ? "enabled" : "disabled"), user.getDisplayName()));
        }
    }
}
