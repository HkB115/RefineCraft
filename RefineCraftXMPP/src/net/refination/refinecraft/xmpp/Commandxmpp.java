package net.refination.refinecraft.xmpp;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Console;
import net.refination.refinecraft.commands.RefineCraftCommand;
import net.refination.refinecraft.commands.NotEnoughArgumentsException;
import org.bukkit.Server;


public class Commandxmpp extends RefineCraftCommand {
    public Commandxmpp() {
        super("xmpp");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws NotEnoughArgumentsException {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String address = RefineCraftXMPP.getInstance().getAddress(args[0]);
        if (address == null) {
            sender.sendMessage("§cThere are no players matching that name.");
        } else {
            final String message = getFinalArg(args, 1);
            final String senderName = sender.isPlayer() ? RC.getUser(sender.getPlayer()).getDisplayName() : Console.NAME;
            sender.sendMessage("[" + senderName + ">" + address + "] " + message);
            if (!RefineCraftXMPP.getInstance().sendMessage(address, "[" + senderName + "] " + message)) {
                sender.sendMessage("§cError sending message.");
            }
        }
    }
}
