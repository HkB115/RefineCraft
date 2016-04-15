package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandsetjail extends RefineCraftCommand {
    public Commandsetjail() {
        super("setjail");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        RC.getJails().setJail(args[0], user.getLocation());
        user.sendMessage(tl("jailSet", StringUtil.sanitizeString(args[0])));

    }
}
