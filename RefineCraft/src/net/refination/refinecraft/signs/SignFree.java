package net.refination.refinecraft.signs;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.refination.refinecraft.I18n.tl;


public class SignFree extends RefineCraftSign {
    public SignFree() {
        super("Free");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        try {
            ItemStack item = getItemStack(sign.getLine(1), 1, RC);
            item = getItemMeta(item, sign.getLine(2), RC);
            item = getItemMeta(item, sign.getLine(3), RC);
        } catch (SignException ex) {
            sign.setLine(1, "§c<item>");
            throw new SignException(ex.getMessage(), ex);
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        ItemStack itemStack = getItemStack(sign.getLine(1), 1, RC);
        itemStack = getItemMeta(itemStack, sign.getLine(2), RC);
        final ItemStack item = getItemMeta(itemStack, sign.getLine(3), RC);

        if (item.getType() == Material.AIR) {
            throw new SignException(tl("cantSpawnItem", "Air"));
        }

        item.setAmount(item.getType().getMaxStackSize());

        ItemMeta meta = item.getItemMeta();

        final String displayName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();

        Inventory invent = RC.getServer().createInventory(player.getBase(), 36, displayName);
        for (int i = 0; i < 36; i++) {
            invent.addItem(item);
        }
        player.getBase().openInventory(invent);
        Trade.log("Sign", "Free", "Interact", username, null, username, new Trade(item, RC), sign.getBlock().getLocation(), RC);
        return true;
    }
}
