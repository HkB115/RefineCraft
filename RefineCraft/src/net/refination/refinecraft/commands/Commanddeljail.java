package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commanddeljail extends RefineCraftCommand {
    public Commanddeljail() {
        super("deljail");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        RC.getJails().removeJail(args[0]);
        sender.sendMessage(tl("deleteJail", args[0]));
    }
}
