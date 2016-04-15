package net.refination.refinecraft.commands;

import net.refination.refinecraft.Enchantments;
import net.refination.refinecraft.MetaItemStack;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static net.refination.refinecraft.I18n.tl;


public class Commandenchant extends RefineCraftCommand {
    public Commandenchant() {
        super("enchant");
    }

    //TODO: Implement charge costs: final Trade charge = new Trade("enchant-" + enchantmentName, RC);
    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack stack = user.getBase().getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            throw new Exception(tl("nothingInHand"));
        }
        if (args.length == 0) {
            final Set<String> enchantmentslist = new TreeSet<>();
            for (Map.Entry<String, Enchantment> entry : Enchantments.entrySet()) {
                final String enchantmentName = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
                if (enchantmentslist.contains(enchantmentName) || (user.isAuthorized("refinecraft.enchantments." + enchantmentName) && entry.getValue().canEnchantItem(stack))) {
                    enchantmentslist.add(entry.getKey());
                    //enchantmentslist.add(enchantmentName);
                }
            }
            throw new NotEnoughArgumentsException(tl("enchantments", StringUtil.joinList(enchantmentslist.toArray())));
        }

        int level = -1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                level = -1;
            }
        }

        final boolean allowUnsafe = RC.getSettings().allowUnsafeEnchantments() && user.isAuthorized("refinecraft.enchantments.allowunsafe");

        final MetaItemStack metaStack = new MetaItemStack(stack);
        final Enchantment enchantment = metaStack.getEnchantment(user, args[0]);
        metaStack.addEnchantment(user.getSource(), allowUnsafe, enchantment, level);
        user.getBase().getInventory().setItemInHand(metaStack.getItemStack());

        user.getBase().updateInventory();
        final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
        if (level == 0) {
            user.sendMessage(tl("enchantmentRemoved", enchantmentName.replace('_', ' ')));
        } else {
            user.sendMessage(tl("enchantmentApplied", enchantmentName.replace('_', ' ')));
        }
    }
}
