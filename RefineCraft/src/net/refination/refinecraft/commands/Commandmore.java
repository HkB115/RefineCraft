package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandmore extends RefineCraftCommand {
    public Commandmore() {
        super("more");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack stack = user.getBase().getItemInHand();
        if (stack == null) {
            throw new Exception(tl("cantSpawnItem", "Air"));
        }
        if (stack.getAmount() >= ((user.isAuthorized("refinecraft.oversizedstacks")) ? RC.getSettings().getOversizedStackSize() : stack.getMaxStackSize())) {
            throw new Exception(tl("fullStack"));
        }
        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (RC.getSettings().permissionBasedItemSpawn() ? (!user.isAuthorized("refinecraft.itemspawn.item-all") && !user.isAuthorized("refinecraft.itemspawn.item-" + itemname) && !user.isAuthorized("refinecraft.itemspawn.item-" + stack.getTypeId())) : (!user.isAuthorized("refinecraft.itemspawn.exempt") && !user.canSpawnItem(stack.getTypeId()))) {
            throw new Exception(tl("cantSpawnItem", itemname));
        }
        if (user.isAuthorized("refinecraft.oversizedstacks")) {
            stack.setAmount(RC.getSettings().getOversizedStackSize());
        } else {
            stack.setAmount(stack.getMaxStackSize());
        }
        user.getBase().updateInventory();
    }
}