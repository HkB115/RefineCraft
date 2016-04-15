package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.World;

import static net.refination.refinecraft.I18n.tl;


public class Commandthunder extends RefineCraftCommand {
    public Commandthunder() {
        super("thunder");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final World world = user.getWorld();
        final boolean setThunder = args[0].equalsIgnoreCase("true");
        if (args.length > 1) {

            world.setThundering(setThunder);
            world.setThunderDuration(Integer.parseInt(args[1]) * 20);
            user.sendMessage(tl("thunderDuration", (setThunder ? tl("enabled") : tl("disabled")), Integer.parseInt(args[1])));

        } else {
            world.setThundering(setThunder);
            user.sendMessage(tl("thunder", setThunder ? tl("enabled") : tl("disabled")));
        }

    }
}
