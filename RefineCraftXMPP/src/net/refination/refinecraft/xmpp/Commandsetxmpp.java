package net.refination.refinecraft.xmpp;

import net.refination.refinecraft.User;
import net.refination.refinecraft.commands.RefineCraftCommand;
import net.refination.refinecraft.commands.NotEnoughArgumentsException;
import org.bukkit.Server;


public class Commandsetxmpp extends RefineCraftCommand {
    public Commandsetxmpp() {
        super("setxmpp");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws NotEnoughArgumentsException {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        RefineCraftXMPP.getInstance().setAddress(user.getBase(), args[0]);
        user.sendMessage("XMPP address set to " + args[0]);
    }
}
