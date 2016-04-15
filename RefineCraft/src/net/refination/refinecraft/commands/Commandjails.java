package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Server;


public class Commandjails extends RefineCraftCommand {
    public Commandjails() {
        super("jails");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sender.sendMessage("§7" + StringUtil.joinList(" ", RC.getJails().getList()));
    }
}
