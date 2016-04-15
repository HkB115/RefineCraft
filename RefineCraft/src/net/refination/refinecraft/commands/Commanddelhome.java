package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commanddelhome extends RefineCraftCommand {
    public Commanddelhome() {
        super("delhome");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User user = RC.getUser(sender.getPlayer());
        String name;
        String[] expandedArg;

        //Allowing both formats /sethome khobbits house | /sethome khobbits:house
        final String[] nameParts = args[0].split(":");
        if (nameParts[0].length() != args[0].length()) {
            expandedArg = nameParts;
        } else {
            expandedArg = args;
        }

        if (expandedArg.length > 1 && (user == null || user.isAuthorized("refinecraft.delhome.others"))) {
            user = getPlayer(server, expandedArg, 0, true, true);
            name = expandedArg[1];
        } else if (user == null) {
            throw new NotEnoughArgumentsException();
        } else {
            name = expandedArg[0];
        }

        if (name.equalsIgnoreCase("bed")) {
            throw new Exception(tl("invalidHomeName"));
        }

        user.delHome(name.toLowerCase(Locale.ENGLISH));
        sender.sendMessage(tl("deleteHome", name));
    }
}
