package net.refination.refinecraft;

import net.refination.refinecraft.Trade.OverflowType;
import net.refination.refinecraft.commands.NoChargeException;
import net.refination.refinecraft.craftbukkit.InventoryWorkaround;
import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.KeywordReplacer;
import net.refination.refinecraft.textreader.SimpleTextInput;
import net.refination.refinecraft.utils.DateUtil;
import net.refination.refinecraft.utils.NumberUtil;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;

import static net.refination.refinecraft.I18n.capitalCase;
import static net.refination.refinecraft.I18n.tl;


public class Kit {
    final InterfaceRefineCraft RC;
    final String kitName;
    final Map<String, Object> kit;
    final Trade charge;

    public Kit(final String kitName, final InterfaceRefineCraft RC) throws Exception {
        this.kitName = kitName;
        this.RC = RC;
        this.kit = RC.getSettings().getKit(kitName);
        this.charge = new Trade("kit-" + kitName, new Trade("kit-kit", RC), RC);

        if (kit == null) {
            throw new Exception(tl("kitNotFound"));
        }
    }

    //TODO: Convert this to use one of the new text classes?
    public static String listKits(final InterfaceRefineCraft RC, final User user) throws Exception {
        try {
            final ConfigurationSection kits = RC.getSettings().getKits();
            final StringBuilder list = new StringBuilder();
            for (String kitItem : kits.getKeys(false)) {
                if (user == null) {
                    list.append(" ").append(capitalCase(kitItem));
                } else if (user.isAuthorized("refinecraft.kits." + kitItem.toLowerCase(Locale.ENGLISH))) {
                    String cost = "";
                    String name = capitalCase(kitItem);
                    BigDecimal costPrice = new Trade("kit-" + kitItem.toLowerCase(Locale.ENGLISH), RC).getCommandCost(user);
                    if (costPrice.signum() > 0) {
                        cost = tl("kitCost", NumberUtil.displayCurrency(costPrice, RC));
                    }

                    Kit kit = new Kit(kitItem, RC);
                    double nextUse = kit.getNextUse(user);
                    if (nextUse == -1 && RC.getSettings().isSkippingUsedOneTimeKitsFromKitList()) {
                        continue;
                    } else if (nextUse != 0) {
                        name = tl("kitDelay", name);
                    }

                    list.append(" ").append(name).append(cost);
                }
            }
            return list.toString().trim();
        } catch (Exception ex) {
            throw new Exception(tl("kitError"), ex);
        }

    }

    public String getName() {
        return kitName;
    }

    public void checkPerms(final User user) throws Exception {
        if (!user.isAuthorized("refinecraft.kits." + kitName)) {
            throw new Exception(tl("noKitPermission", "refinecraft.kits." + kitName));
        }
    }

    public void checkDelay(final User user) throws Exception {
        long nextUse = getNextUse(user);

        if (nextUse == 0L) {
            return;
        } else if (nextUse < 0L) {
            user.sendMessage(tl("kitOnce"));
            throw new NoChargeException();
        } else {
            user.sendMessage(tl("kitTimed", DateUtil.formatDateDiff(nextUse)));
            throw new NoChargeException();
        }
    }

    public void checkAffordable(final User user) throws Exception {
        charge.isAffordableFor(user);
    }

    public void setTime(final User user) throws Exception {
        final Calendar time = new GregorianCalendar();
        user.setKitTimestamp(kitName, time.getTimeInMillis());
    }

    public void chargeUser(final User user) throws Exception {
        charge.charge(user);
    }

    public long getNextUse(final User user) throws Exception {
        if (user.isAuthorized("refinecraft.kit.exemptdelay")) {
            return 0L;
        }

        final Calendar time = new GregorianCalendar();

        double delay = 0;
        try {
            // Make sure delay is valid
            delay = kit.containsKey("delay") ? ((Number) kit.get("delay")).doubleValue() : 0.0d;
        } catch (Exception e) {
            throw new Exception(tl("kitError2"));
        }

        // When was the last kit used?
        final long lastTime = user.getKitTimestamp(kitName);

        // When can be use the kit again?
        final Calendar delayTime = new GregorianCalendar();
        delayTime.setTimeInMillis(lastTime);
        delayTime.add(Calendar.SECOND, (int) delay);
        delayTime.add(Calendar.MILLISECOND, (int) ((delay * 1000.0) % 1000.0));

        if (lastTime == 0L || lastTime > time.getTimeInMillis()) {
            // If we have no record of kit use, or its corrupted, give them benefit of the doubt.
            return 0L;
        } else if (delay < 0d) {
            // If the kit has a negative kit time, it can only be used once.
            return -1;
        } else if (delayTime.before(time)) {
            // If the kit was used in the past, but outside the delay time, it can be used.
            return 0L;
        } else {
            // If the kit has been used recently, return the next time it can be used.
            return delayTime.getTimeInMillis();
        }
    }

    @Deprecated
    public List<String> getItems(final User user) throws Exception {
        return getItems();
    }

    public List<String> getItems() throws Exception {
        if (kit == null) {
            throw new Exception(tl("kitNotFound"));
        }
        try {
            final List<String> itemList = new ArrayList<String>();
            final Object kitItems = kit.get("items");
            if (kitItems instanceof List) {
                for (Object item : (List) kitItems) {
                    if (item instanceof String) {
                        itemList.add(item.toString());
                        continue;
                    }
                    throw new Exception("Invalid kit item: " + item.toString());
                }
                return itemList;
            }
            throw new Exception("Invalid item list");
        } catch (Exception e) {
            RC.getLogger().log(Level.WARNING, "Error parsing kit " + kitName + ": " + e.getMessage());
            throw new Exception(tl("kitError2"), e);
        }
    }

    public void expandItems(final User user) throws Exception {
        expandItems(user, getItems(user));
    }

    public void expandItems(final User user, final List<String> items) throws Exception {
        try {
            InterfaceText input = new SimpleTextInput(items);
            InterfaceText output = new KeywordReplacer(input, user.getSource(), RC);

            boolean spew = false;
            final boolean allowUnsafe = RC.getSettings().allowUnsafeEnchantments();
            for (String kitItem : output.getLines()) {
                if (kitItem.startsWith(RC.getSettings().getCurrencySymbol())) {
                    BigDecimal value = new BigDecimal(kitItem.substring(RC.getSettings().getCurrencySymbol().length()).trim());
                    Trade t = new Trade(value, RC);
                    t.pay(user, OverflowType.DROP);
                    continue;
                }

                if (kitItem.startsWith("/")) {
                    String command = kitItem.substring(1);
                    String name = user.getName();
                    command = command.replace("{player}", name);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    continue;
                }

                final String[] parts = kitItem.split(" +");
                final ItemStack parseStack = RC.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);

                if (parseStack.getType() == Material.AIR) {
                    continue;
                }

                final MetaItemStack metaStack = new MetaItemStack(parseStack);

                if (parts.length > 2) {
                    // We pass a null sender here because kits should not do perm checks
                    metaStack.parseStringMeta(null, allowUnsafe, parts, 2, RC);
                }

                final Map<Integer, ItemStack> overfilled;
                final boolean allowOversizedStacks = user.isAuthorized("refinecraft.oversizedstacks");
                if (allowOversizedStacks) {
                    overfilled = InventoryWorkaround.addOversizedItems(user.getBase().getInventory(), RC.getSettings().getOversizedStackSize(), metaStack.getItemStack());
                } else {
                    overfilled = InventoryWorkaround.addItems(user.getBase().getInventory(), metaStack.getItemStack());
                }
                for (ItemStack itemStack : overfilled.values()) {
                    int spillAmount = itemStack.getAmount();
                    if (!allowOversizedStacks) {
                        itemStack.setAmount(spillAmount < itemStack.getMaxStackSize() ? spillAmount : itemStack.getMaxStackSize());
                    }
                    while (spillAmount > 0) {
                        user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
                        spillAmount -= itemStack.getAmount();
                    }
                    spew = true;
                }
            }
            user.getBase().updateInventory();
            if (spew) {
                user.sendMessage(tl("kitInvFull"));
            }
        } catch (Exception e) {
            user.getBase().updateInventory();
            RC.getLogger().log(Level.WARNING, e.getMessage());
            throw new Exception(tl("kitError2"), e);
        }
    }
}
