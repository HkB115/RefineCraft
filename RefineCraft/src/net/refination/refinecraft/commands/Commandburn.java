package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandburn extends RefineCraftCommand {
    public Commandburn() {
        super("burn");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        if (args[0].trim().length() < 2) {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, sender, args, 0);
        user.getBase().setFireTicks(Integer.parseInt(args[1]) * 20);
        sender.sendMessage(tl("burnMsg", user.getDisplayName(), Integer.parseInt(args[1])));
    }
}
