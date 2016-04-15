package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.InterfaceRefineCraftModule;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Server;
import org.bukkit.command.Command;


public interface InterfaceRefineCraftCommand {
    String getName();

    void run(Server server, User user, String commandLabel, Command cmd, String[] args) throws Exception;

    void run(Server server, CommandSource sender, String commandLabel, Command cmd, String[] args) throws Exception;

    void setRefineCraft(InterfaceRefineCraft RC);

    void setRefineCraftModule(InterfaceRefineCraftModule module);
}
