package net.refination.refinecraft.spawn;

import net.refination.refinecraft.User;
import net.refination.refinecraft.commands.RefineCraftCommand;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandsetspawn extends RefineCraftCommand {
    public Commandsetspawn() {
        super("setspawn");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final String group = args.length > 0 ? getFinalArg(args, 0) : "default";
        ((SpawnStorage) module).setSpawn(user.getLocation(), group);
        user.sendMessage(tl("spawnSet", group));
    }
}
