package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Enchantments;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class SignEnchant extends RefineCraftSign {
    public SignEnchant() {
        super("Enchant");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final ItemStack stack;
        try {
            stack = sign.getLine(1).equals("*") || sign.getLine(1).equalsIgnoreCase("any") ? null : getItemStack(sign.getLine(1), 1, RC);
        } catch (SignException e) {
            sign.setLine(1, "§c<item|any>");
            throw e;
        }
        final String[] enchantLevel = sign.getLine(2).split(":");
        if (enchantLevel.length != 2) {
            sign.setLine(2, "§c<enchant>");
            throw new SignException(tl("invalidSignLine", 3));
        }
        final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
        if (enchantment == null) {
            sign.setLine(2, "§c<enchant>");
            throw new SignException(tl("enchantmentNotFound"));
        }
        int level;
        try {
            level = Integer.parseInt(enchantLevel[1]);
        } catch (NumberFormatException ex) {
            sign.setLine(2, "§c<enchant>");
            throw new SignException(ex.getMessage(), ex);
        }
        final boolean allowUnsafe = RC.getSettings().allowUnsafeEnchantments() && player.isAuthorized("refinecraft.enchantments.allowunsafe") && player.isAuthorized("refinecraft.signs.enchant.allowunsafe");
        if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel())) {
            level = enchantment.getMaxLevel();
            sign.setLine(2, enchantLevel[0] + ":" + level);
        }
        try {
            if (stack != null) {
                if (allowUnsafe) {
                    stack.addUnsafeEnchantment(enchantment, level);
                } else {
                    stack.addEnchantment(enchantment, level);
                }
            }
        } catch (Throwable ex) {
            throw new SignException(ex.getMessage(), ex);
        }
        getTrade(sign, 3, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(InterfaceSign sign, User player, String username, InterfaceRefineCraft RC) throws SignException, ChargeException {
        final ItemStack search = sign.getLine(1).equals("*") || sign.getLine(1).equalsIgnoreCase("any") ? null : getItemStack(sign.getLine(1), 1, RC);
        final Trade charge = getTrade(sign, 3, RC);
        charge.isAffordableFor(player);
        final String[] enchantLevel = sign.getLine(2).split(":");
        if (enchantLevel.length != 2) {
            throw new SignException(tl("invalidSignLine", 3));
        }
        final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
        if (enchantment == null) {
            throw new SignException(tl("enchantmentNotFound"));
        }
        int level;
        try {
            level = Integer.parseInt(enchantLevel[1]);
        } catch (NumberFormatException ex) {
            level = enchantment.getMaxLevel();
        }

        final ItemStack playerHand = player.getBase().getItemInHand();
        if (playerHand == null || playerHand.getAmount() != 1 || (playerHand.containsEnchantment(enchantment) && playerHand.getEnchantmentLevel(enchantment) == level)) {
            throw new SignException(tl("missingItems", 1, sign.getLine(1)));
        }
        if (search != null && playerHand.getType() != search.getType()) {
            throw new SignException(tl("missingItems", 1, search.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ')));
        }

        final ItemStack toEnchant = playerHand;
        try {
            if (level == 0) {
                toEnchant.removeEnchantment(enchantment);
            } else {
                if (RC.getSettings().allowUnsafeEnchantments() && player.isAuthorized("refinecraft.signs.enchant.allowunsafe")) {
                    toEnchant.addUnsafeEnchantment(enchantment, level);
                } else {
                    toEnchant.addEnchantment(enchantment, level);
                }
            }
        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
        if (level == 0) {
            player.sendMessage(tl("enchantmentRemoved", enchantmentName.replace('_', ' ')));
        } else {
            player.sendMessage(tl("enchantmentApplied", enchantmentName.replace('_', ' ')));
        }

        charge.charge(player);
        Trade.log("Sign", "Enchant", "Interact", username, charge, username, charge, sign.getBlock().getLocation(), RC);
        player.getBase().updateInventory();
        return true;
    }
}
