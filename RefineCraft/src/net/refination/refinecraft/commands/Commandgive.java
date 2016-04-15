package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.MetaItemStack;
import net.refination.refinecraft.User;
import net.refination.refinecraft.craftbukkit.InventoryWorkaround;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

import static net.refination.refinecraft.I18n.tl;


public class Commandgive extends RefineCraftCommand {
    public Commandgive() {
        super("give");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        ItemStack stack = RC.getItemDb().get(args[1]);

        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (sender.isPlayer() && (RC.getSettings().permissionBasedItemSpawn() ? (!RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.itemspawn.item-all") && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.itemspawn.item-" + itemname) && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.itemspawn.item-" + stack.getTypeId())) : (!RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.itemspawn.exempt") && !RC.getUser(sender.getPlayer()).canSpawnItem(stack.getTypeId())))) {
            throw new Exception(tl("cantSpawnItem", itemname));
        }

        final User giveTo = getPlayer(server, sender, args, 0);

        try {
            if (args.length > 3 && NumberUtil.isInt(args[2]) && NumberUtil.isInt(args[3])) {
                stack.setAmount(Integer.parseInt(args[2]));
                stack.setDurability(Short.parseShort(args[3]));
            } else if (args.length > 2 && Integer.parseInt(args[2]) > 0) {
                stack.setAmount(Integer.parseInt(args[2]));
            } else if (RC.getSettings().getDefaultStackSize() > 0) {
                stack.setAmount(RC.getSettings().getDefaultStackSize());
            } else if (RC.getSettings().getOversizedStackSize() > 0 && giveTo.isAuthorized("refinecraft.oversizedstacks")) {
                stack.setAmount(RC.getSettings().getOversizedStackSize());
            }
        } catch (NumberFormatException e) {
            throw new NotEnoughArgumentsException();
        }

        MetaItemStack metaStack = new MetaItemStack(stack);
        if (!metaStack.canSpawn(RC)) {
            throw new Exception(tl("unableToSpawnItem", itemname));
        }

        if (args.length > 3) {
            boolean allowUnsafe = RC.getSettings().allowUnsafeEnchantments();
            if (allowUnsafe && sender.isPlayer() && !RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.enchantments.allowunsafe")) {
                allowUnsafe = false;
            }

            int metaStart = NumberUtil.isInt(args[3]) ? 4 : 3;

            if (args.length > metaStart) {
                metaStack.parseStringMeta(sender, allowUnsafe, args, metaStart, RC);
            }

            stack = metaStack.getItemStack();
        }

        if (stack.getType() == Material.AIR) {
            throw new Exception(tl("cantSpawnItem", "Air"));
        }

        final String itemName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        sender.sendMessage(tl("giveSpawn", stack.getAmount(), itemName, giveTo.getDisplayName()));

        Map<Integer, ItemStack> leftovers;

        if (giveTo.isAuthorized("refinecraft.oversizedstacks")) {
            leftovers = InventoryWorkaround.addOversizedItems(giveTo.getBase().getInventory(), RC.getSettings().getOversizedStackSize(), stack);
        } else {
            leftovers = InventoryWorkaround.addItems(giveTo.getBase().getInventory(), stack);
        }

        boolean isDropItemsIfFull = RC.getSettings().isDropItemsIfFull();

        for (ItemStack item : leftovers.values()) {
            if (isDropItemsIfFull) {
                World w = giveTo.getWorld();
                w.dropItemNaturally(giveTo.getLocation(), item);
            } else {
                sender.sendMessage(tl("giveSpawnFailure", item.getAmount(), itemName, giveTo.getDisplayName()));
            }
        }

        giveTo.getBase().updateInventory();
    }
}
