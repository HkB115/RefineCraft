package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandbroadcast extends RefineCraftCommand {
    public Commandbroadcast() {
        super("broadcast");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        sendBroadcast(user.getDisplayName(), args);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sendBroadcast(sender.getSender().getName(), args);
    }

    private void sendBroadcast(final String name, final String[] args) throws NotEnoughArgumentsException {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        RC.broadcastMessage(tl("broadcast", FormatUtil.replaceFormat(getFinalArg(args, 0)).replace("\\n", "\n"), name));
    }
}
