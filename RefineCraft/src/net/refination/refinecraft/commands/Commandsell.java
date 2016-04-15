package net.refination.refinecraft.commands;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandsell extends RefineCraftCommand {
    public Commandsell() {
        super("sell");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        BigDecimal totalWorth = BigDecimal.ZERO;
        String type = "";
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        if (args[0].equalsIgnoreCase("hand") && !user.isAuthorized("refinecraft.sell.hand")) {
            throw new Exception(tl("sellHandPermission"));
        } else if ((args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) && !user.isAuthorized("refinecraft.sell.bulk")) {
            throw new Exception(tl("sellBulkPermission"));
        }

        List<ItemStack> is = RC.getItemDb().getMatching(user, args);
        int count = 0;

        boolean isBulk = is.size() > 1;

        for (ItemStack stack : is) {
            try {
                if (stack.getAmount() > 0) {
                    totalWorth = totalWorth.add(sellItem(user, stack, args, isBulk));
                    stack = stack.clone();
                    count++;
                    for (ItemStack zeroStack : is) {
                        if (zeroStack.isSimilar(stack)) {
                            zeroStack.setAmount(0);
                        }
                    }
                }
            } catch (Exception e) {
                if (!isBulk) {
                    throw e;
                }
            }
        }
        if (count != 1) {
            if (args[0].equalsIgnoreCase("blocks")) {
                user.sendMessage(tl("totalWorthBlocks", type, NumberUtil.displayCurrency(totalWorth, RC)));
            } else {
                user.sendMessage(tl("totalWorthAll", type, NumberUtil.displayCurrency(totalWorth, RC)));
            }
        }
    }

    private BigDecimal sellItem(User user, ItemStack is, String[] args, boolean isBulkSell) throws Exception {
        int amount = RC.getWorth().getAmount(RC, user, is, args, isBulkSell);
        BigDecimal worth = RC.getWorth().getPrice(is);

        if (worth == null) {
            throw new Exception(tl("itemCannotBeSold"));
        }

        if (amount <= 0) {
            if (!isBulkSell) {
                user.sendMessage(tl("itemSold", NumberUtil.displayCurrency(BigDecimal.ZERO, RC), BigDecimal.ZERO, is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(worth, RC)));
            }
            return BigDecimal.ZERO;
        }

        BigDecimal result = worth.multiply(BigDecimal.valueOf(amount));

        //TODO: Prices for Enchantments
        final ItemStack ris = is.clone();
        ris.setAmount(amount);
        if (!user.getBase().getInventory().containsAtLeast(ris, amount)) {
            // This should never happen.
            throw new IllegalStateException("Trying to remove more items than are available.");
        }
        user.getBase().getInventory().removeItem(ris);
        user.getBase().updateInventory();
        Trade.log("Command", "Sell", "Item", user.getName(), new Trade(ris, RC), user.getName(), new Trade(result, RC), user.getLocation(), RC);
        user.giveMoney(result);
        user.sendMessage(tl("itemSold", NumberUtil.displayCurrency(result, RC), amount, is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(worth, RC)));
        logger.log(Level.INFO, tl("itemSoldConsole", user.getDisplayName(), is.getType().toString().toLowerCase(Locale.ENGLISH), NumberUtil.displayCurrency(result, RC), amount, NumberUtil.displayCurrency(worth, RC)));
        return result;
    }
}
