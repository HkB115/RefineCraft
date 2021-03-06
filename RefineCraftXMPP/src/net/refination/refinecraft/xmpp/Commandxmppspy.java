package net.refination.refinecraft.xmpp;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.commands.RefineCraftCommand;
import net.refination.refinecraft.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;


public class Commandxmppspy extends RefineCraftCommand {
    public Commandxmppspy() {
        super("xmppspy");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws NotEnoughArgumentsException {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final List<Player> matches = server.matchPlayer(args[0]);

        if (matches.isEmpty()) {
            sender.sendMessage("§cThere are no players matching that name.");
        }

        for (Player p : matches) {
            try {
                final boolean toggle = RefineCraftXMPP.getInstance().toggleSpy(p);
                sender.sendMessage("XMPP Spy " + (toggle ? "enabled" : "disabled") + " for " + p.getDisplayName());
            } catch (Exception ex) {
                sender.sendMessage("Error: " + ex.getMessage());
            }
        }
    }
}