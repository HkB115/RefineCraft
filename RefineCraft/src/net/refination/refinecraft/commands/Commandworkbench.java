package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;


public class Commandworkbench extends RefineCraftCommand {
    public Commandworkbench() {
        super("workbench");
    }


    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.getBase().openWorkbench(null, true);
    }
}