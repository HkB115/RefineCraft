package net.refination.refinecraft.api;

import net.refination.refinecraft.User;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public interface InterfaceItemDb {
    ItemStack get(final String name, final int quantity) throws Exception;

    ItemStack get(final String name) throws Exception;

    String names(ItemStack item);

    String name(ItemStack item);

    List<ItemStack> getMatching(User user, String[] args) throws Exception;

    String serialize(ItemStack is);
}
