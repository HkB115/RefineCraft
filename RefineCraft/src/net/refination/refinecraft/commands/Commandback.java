package net.refination.refinecraft.commands;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandback extends RefineCraftCommand {
    public Commandback() {
        super("back");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.getLastLocation() == null) {
            throw new Exception(tl("noLocationFound"));
        }
        if (user.getWorld() != user.getLastLocation().getWorld() && RC.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("refinecraft.worlds." + user.getLastLocation().getWorld().getName())) {
            throw new Exception(tl("noPerm", "refinecraft.worlds." + user.getLastLocation().getWorld().getName()));
        }
        final Trade charge = new Trade(this.getName(), RC);
        charge.isAffordableFor(user);
        user.getTeleport().back(charge);
        throw new NoChargeException();
    }
}
