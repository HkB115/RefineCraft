package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Server;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandhelpop extends RefineCraftCommand {
    public Commandhelpop() {
        super("helpop");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.setDisplayNick();
        final String message = sendMessage(server, user.getSource(), user.getDisplayName(), args);
        if (!user.isAuthorized("refinecraft.helpop.receive")) {
            user.sendMessage(message);
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sendMessage(server, sender, Console.NAME, args);
    }

    private String sendMessage(final Server server, final CommandSource sender, final String from, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final String message = tl("helpOp", from, FormatUtil.stripFormat(getFinalArg(args, 0)));
        server.getLogger().log(Level.INFO, message);
        RC.broadcastMessage("refinecraft.helpop.receive", message);
        return message;
    }
}
