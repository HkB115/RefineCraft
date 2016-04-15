package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.api.events.GodStatusChangeEvent;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandgod extends RefineCraftToggleCommand {
    public Commandgod() {
        super("god", "refinecraft.god.others");
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
            enabled = !user.isGodModeEnabled();
        }

        final User controller = sender.isPlayer() ? RC.getUser(sender.getPlayer()) : null;
        final GodStatusChangeEvent godEvent = new GodStatusChangeEvent(controller, user, enabled);
        RC.getServer().getPluginManager().callEvent(godEvent);
        if (!godEvent.isCancelled()) {
            user.setGodModeEnabled(enabled);

            if (enabled && user.getBase().getHealth() != 0) {
                user.getBase().setHealth(user.getBase().getMaxHealth());
                user.getBase().setFoodLevel(20);
            }

            user.sendMessage(tl("godMode", enabled ? tl("enabled") : tl("disabled")));
            if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
                sender.sendMessage(tl("godMode", tl(enabled ? "godEnabledFor" : "godDisabledFor", user.getDisplayName())));
            }
        }
    }
}
