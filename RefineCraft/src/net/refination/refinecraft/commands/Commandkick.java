package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Server;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandkick extends RefineCraftCommand {
    public Commandkick() {
        super("kick");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, args, 0, true, false);
        if (sender.isPlayer()) {
            User user = RC.getUser(sender.getPlayer());
            if (target.isHidden(sender.getPlayer()) && !user.canInteractGhosted() && !sender.getPlayer().canSee(target.getBase())) {
                throw new PlayerNotFoundException();
            }

            if (target.isAuthorized("refinecraft.kick.exempt")) {
                throw new Exception(tl("kickExempt"));
            }
        }

        String kickReason = args.length > 1 ? getFinalArg(args, 1) : tl("kickDefault");
        kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

        target.getBase().kickPlayer(kickReason);
        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;

        server.getLogger().log(Level.INFO, tl("playerKicked", senderName, target.getName(), kickReason));
        RC.broadcastMessage("refinecraft.kick.notify", tl("playerKicked", senderName, target.getName(), kickReason));
    }
}
