package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.User;
import net.refination.refinecraft.messaging.InterfaceMessageRecipient;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandreply extends RefineCraftCommand {
    public Commandreply() {
        super("reply");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 0);
        InterfaceMessageRecipient messageSender;

        if (sender.isPlayer()) {
            User user = RC.getUser(sender.getPlayer());
            message = FormatUtil.formatMessage(user, "refinecraft.msg", message);
            messageSender = user;
        } else {
            message = FormatUtil.replaceFormat(message);
            messageSender = Console.getInstance();
        }

        final InterfaceMessageRecipient target = messageSender.getReplyRecipient();
        // Check to make sure the sender does have a quick-reply recipient
        if (target == null) {
            throw new Exception(tl("foreverAlone"));
        }
        messageSender.sendMessage(target, message);
    }
}
