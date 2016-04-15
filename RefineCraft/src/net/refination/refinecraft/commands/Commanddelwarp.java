package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commanddelwarp extends RefineCraftCommand {
    public Commanddelwarp() {
        super("delwarp");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        RC.getWarps().removeWarp(args[0]);
        sender.sendMessage(tl("deleteWarp", args[0]));
    }
}
