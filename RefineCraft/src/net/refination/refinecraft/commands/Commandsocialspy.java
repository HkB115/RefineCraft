package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandsocialspy extends RefineCraftToggleCommand {
    public Commandsocialspy() {
        super("socialspy", "refinecraft.socialspy.others");
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
    void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException {
        if (enabled == null) {
            enabled = !user.isSocialSpyEnabled();
        }

        user.setSocialSpyEnabled(enabled);


        user.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        }
    }
}
