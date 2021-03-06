package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static net.refination.refinecraft.I18n.tl;


public class Commandext extends RefineCraftLoopCommand {
    public Commandext() {
        super("ext");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            extPlayer(user.getBase());
            user.sendMessage(tl("extinguish"));
            return;
        }

        loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) {
        extPlayer(player.getBase());
        sender.sendMessage(tl("extinguishOthers", player.getDisplayName()));
    }

    private void extPlayer(final Player player) {
        player.setFireTicks(0);
    }
}
