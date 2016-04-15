package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;


public class Commandinvsee extends RefineCraftCommand {
    public Commandinvsee() {
        super("invsee");
    }

    //This method has a hidden param, which if given will display the equip slots. #easteregg
    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User invUser = getPlayer(server, user, args, 0);
        Inventory inv;

        if (args.length > 1 && user.isAuthorized("refinecraft.invsee.equip")) {
            inv = server.createInventory(invUser.getBase(), 9, "Equipped");
            inv.setContents(invUser.getBase().getInventory().getArmorContents());
        } else {
            inv = invUser.getBase().getInventory();
        }
        user.getBase().closeInventory();
        user.getBase().openInventory(inv);
        user.setInvSee(true);
    }
}
