package net.refination.refinecraft.commands;

import static net.refination.refinecraft.I18n.tl;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import net.refination.refinecraft.messaging.InterfaceMessageRecipient;
import net.refination.refinecraft.utils.FormatUtil;

import org.bukkit.Server;


public class Commandmsg extends RefineCraftLoopCommand {

    public Commandmsg() {
        super("msg");
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 2 || args[0].trim().length() < 2 || args[1].trim().isEmpty()) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 1);
        boolean canWildcard;
        if (sender.isPlayer()) {
            User user = RC.getUser(sender.getPlayer());
            if (user.isMuted()) {
                throw new Exception(tl("voiceSilenced"));
            }
            message = FormatUtil.formatMessage(user, "refinecraft.msg", message);
            canWildcard = user.isAuthorized("refinecraft.msg.multiple");
        } else {
            message = FormatUtil.replaceFormat(message);
            canWildcard = true;
        }

        // Sending messages to console
        if (args[0].equalsIgnoreCase(Console.NAME)) {
            InterfaceMessageRecipient messageSender = sender.isPlayer() ? RC.getUser(sender.getPlayer()) : Console.getInstance();
            messageSender.sendMessage(Console.getInstance(), message);
            return;
        }

        loopOnlinePlayers(server, sender, canWildcard, canWildcard, args[0], new String[]{message});
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User messageReceiver, final String[] args) {
        InterfaceMessageRecipient messageSender = sender.isPlayer() ? RC.getUser(sender.getPlayer()) : Console.getInstance();
        messageSender.sendMessage(messageReceiver, args[0]); // args[0] is the message.
    }
}
