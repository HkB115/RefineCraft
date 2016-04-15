package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandtptoggle extends RefineCraftToggleCommand {
    public Commandtptoggle() {
        super("tptoggle", "refinecraft.tptoggle.others");
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
            enabled = !user.isTeleportEnabled();
        }

        user.setTeleportEnabled(enabled);

        user.sendMessage(enabled ? tl("teleportationEnabled") : tl("teleportationDisabled"));
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendMessage(enabled ? tl("teleportationEnabledFor", user.getDisplayName()) : tl("teleportationDisabledFor", user.getDisplayName()));
        }
    }
}
