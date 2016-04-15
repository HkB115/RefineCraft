package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import static net.refination.refinecraft.I18n.tl;


public class Commandfeed extends RefineCraftLoopCommand {
    public Commandfeed() {
        super("feed");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (!user.isAuthorized("refinecraft.feed.cooldown.bypass")) {
            user.healCooldown();
        }

        if (args.length > 0 && user.isAuthorized("refinecraft.feed.others")) {
            loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
            return;
        }

        feedPlayer(user.getBase());
        user.sendMessage(tl("feed"));
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws PlayerExemptException {
        try {
            feedPlayer(player.getBase());
            sender.sendMessage(tl("feedOther", player.getDisplayName()));
        } catch (QuietAbortException e) {
            //Handle Quietly
        }
    }

    private void feedPlayer(final Player player) throws QuietAbortException {
        final int amount = 30;

        final FoodLevelChangeEvent flce = new FoodLevelChangeEvent(player, amount);
        RC.getServer().getPluginManager().callEvent(flce);
        if (flce.isCancelled()) {
            throw new QuietAbortException();
        }

        player.setFoodLevel(flce.getFoodLevel() > 20 ? 20 : flce.getFoodLevel());
        player.setSaturation(10);
        player.setExhaustion(0F);
    }
}
