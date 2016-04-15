package net.refination.refinecraft.commands;

import net.refination.refinecraft.MetaItemStack;
import net.refination.refinecraft.User;
import net.refination.refinecraft.craftbukkit.InventoryWorkaround;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commanditem extends RefineCraftCommand {
    public Commanditem() {
        super("item");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        ItemStack stack = RC.getItemDb().get(args[0]);

        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (RC.getSettings().permissionBasedItemSpawn() ? (!user.isAuthorized("refinecraft.itemspawn.item-all") && !user.isAuthorized("refinecraft.itemspawn.item-" + itemname) && !user.isAuthorized("refinecraft.itemspawn.item-" + stack.getTypeId())) : (!user.isAuthorized("refinecraft.itemspawn.exempt") && !user.canSpawnItem(stack.getTypeId()))) {
            throw new Exception(tl("cantSpawnItem", itemname));
        }
        try {
            if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
                stack.setAmount(Integer.parseInt(args[1]));
            } else if (RC.getSettings().getDefaultStackSize() > 0) {
                stack.setAmount(RC.getSettings().getDefaultStackSize());
            } else if (RC.getSettings().getOversizedStackSize() > 0 && user.isAuthorized("refinecraft.oversizedstacks")) {
                stack.setAmount(RC.getSettings().getOversizedStackSize());
            }
        } catch (NumberFormatException e) {
            throw new NotEnoughArgumentsException();
        }

        MetaItemStack metaStack = new MetaItemStack(stack);
        if (!metaStack.canSpawn(RC)) {
            throw new Exception(tl("unableToSpawnItem", itemname));
        }

        if (args.length > 2) {
            final boolean allowUnsafe = RC.getSettings().allowUnsafeEnchantments() && user.isAuthorized("refinecraft.enchantments.allowunsafe");

            metaStack.parseStringMeta(user.getSource(), allowUnsafe, args, 2, RC);

            stack = metaStack.getItemStack();
        }


        if (stack.getType() == Material.AIR) {
            throw new Exception(tl("cantSpawnItem", "Air"));
        }

        final String displayName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        user.sendMessage(tl("itemSpawn", stack.getAmount(), displayName));
        if (user.isAuthorized("refinecraft.oversizedstacks")) {
            InventoryWorkaround.addOversizedItems(user.getBase().getInventory(), RC.getSettings().getOversizedStackSize(), stack);
        } else {
            InventoryWorkaround.addItems(user.getBase().getInventory(), stack);
        }
        user.getBase().updateInventory();
    }
}
