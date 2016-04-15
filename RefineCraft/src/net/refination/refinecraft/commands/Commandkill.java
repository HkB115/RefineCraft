package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.refination.refinecraft.I18n.tl;


public class Commandkill extends RefineCraftLoopCommand {
    public Commandkill() {
        super("kill");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, final String[] args) throws PlayerExemptException {
        final Player matchPlayer = user.getBase();
        if (sender.isPlayer() && user.isAuthorized("refinecraft.kill.exempt") && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.kill.force")) {
            throw new PlayerExemptException(tl("killExempt", matchPlayer.getDisplayName()));
        }
        final EntityDamageEvent ede = new EntityDamageEvent(matchPlayer, sender.isPlayer() && sender.getPlayer().getName().equals(matchPlayer.getName()) ? EntityDamageEvent.DamageCause.SUICIDE : EntityDamageEvent.DamageCause.CUSTOM, Short.MAX_VALUE);
        server.getPluginManager().callEvent(ede);
        if (ede.isCancelled() && sender.isPlayer() && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.kill.force")) {
            return;
        }
        ede.getEntity().setLastDamageCause(ede);
        matchPlayer.damage(Short.MAX_VALUE);

        if (matchPlayer.getHealth() > 0) {
            matchPlayer.setHealth(0);
        }

        sender.sendMessage(tl("kill", matchPlayer.getDisplayName()));
    }
}
