package net.refination.refinecraft.commands;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;

// This method contains an undocumented sub command #EasterEgg
public class Commandjump extends RefineCraftCommand {
    public Commandjump() {
        super("jump");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length > 0 && args[0].contains("lock") && user.isAuthorized("refinecraft.jump.lock")) {
            if (user.isFlyClickJump()) {
                user.setRightClickJump(false);
                user.sendMessage("Flying wizard mode disabled");
            } else {
                user.setRightClickJump(true);
                user.sendMessage("Enabling flying wizard mode");
            }
            return;
        }

        Location loc;
        final Location cloc = user.getLocation();

        try {
            loc = LocationUtil.getTarget(user.getBase());
            loc.setYaw(cloc.getYaw());
            loc.setPitch(cloc.getPitch());
            loc.setY(loc.getY() + 1);
        } catch (NullPointerException ex) {
            throw new Exception(tl("jumpError"), ex);
        }

        final Trade charge = new Trade(this.getName(), RC);
        charge.isAffordableFor(user);
        user.getTeleport().teleport(loc, charge, TeleportCause.COMMAND);
        throw new NoChargeException();
    }
}
