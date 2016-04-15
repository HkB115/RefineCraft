package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.entity.LightningStrike;

import java.util.HashSet;

import static net.refination.refinecraft.I18n.tl;


public class Commandlightning extends RefineCraftLoopCommand {
    int power = 5;

    public Commandlightning() {
        super("lightning");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        User user;
        if (sender.isPlayer()) {
            user = RC.getUser(sender.getPlayer());
            if ((args.length < 1 || user != null && !user.isAuthorized("refinecraft.lightning.others"))) {
                user.getWorld().strikeLightning(user.getBase().getTargetBlock((HashSet<Byte>) null, 600).getLocation());
                return;
            }
        }

        if (args.length > 1) {
            try {
                power = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
            }
        }
        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User matchUser, final String[] args) {
        sender.sendMessage(tl("lightningUse", matchUser.getDisplayName()));
        final LightningStrike strike = matchUser.getBase().getWorld().strikeLightningEffect(matchUser.getBase().getLocation());

        if (!matchUser.isGodModeEnabled()) {
            matchUser.getBase().damage(power, strike);
        }
        if (RC.getSettings().warnOnSmite()) {
            matchUser.sendMessage(tl("lightningSmited"));
        }
    }
}
