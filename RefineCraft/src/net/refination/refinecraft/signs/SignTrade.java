package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.Trade.OverflowType;
import net.refination.refinecraft.Trade.TradeType;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.NumberUtil;
import net.refination.api.InterfaceRefineCraft;
import net.refination.api.MaxMoneyException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Map;

import static net.refination.refinecraft.I18n.tl;

//TODO: TL exceptions
public class SignTrade extends RefineCraftSign {
    public SignTrade() {
        super("Trade");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        validateTrade(sign, 1, false, RC);
        validateTrade(sign, 2, true, RC);
        final Trade trade = getTrade(sign, 2, AmountType.ROUNDED, true, RC);
        final Trade charge = getTrade(sign, 1, AmountType.ROUNDED, false, RC);
        if (trade.getType() == charge.getType() && (trade.getType() != TradeType.ITEM || trade.getItemStack().isSimilar(charge.getItemStack()))) {
            throw new SignException("You cannot trade for the same item type.");
        }
        trade.isAffordableFor(player);
        sign.setLine(3, "§8" + username);
        trade.charge(player);
        Trade.log("Sign", "Trade", "Create", username, trade, username, null, sign.getBlock().getLocation(), RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException, MaxMoneyException {
        if (sign.getLine(3).substring(2).equalsIgnoreCase(username)) {
            final Trade store = rechargeSign(sign, RC, player);
            Trade stored;
            try {
                stored = getTrade(sign, 1, AmountType.TOTAL, true, RC);
                subtractAmount(sign, 1, stored, RC);

                Map<Integer, ItemStack> withdraw = stored.pay(player, OverflowType.RETURN);

                if (withdraw == null) {
                    Trade.log("Sign", "Trade", "Withdraw", username, store, username, null, sign.getBlock().getLocation(), RC);
                } else {
                    setAmount(sign, 1, BigDecimal.valueOf(withdraw.get(0).getAmount()), RC);
                    Trade.log("Sign", "Trade", "Withdraw", username, stored, username, new Trade(withdraw.get(0), RC), sign.getBlock().getLocation(), RC);
                }
            } catch (SignException e) {
                if (store == null) {
                    throw new SignException(tl("tradeSignEmptyOwner"), e);
                }
            }
            Trade.log("Sign", "Trade", "Deposit", username, store, username, null, sign.getBlock().getLocation(), RC);
        } else {
            final Trade charge = getTrade(sign, 1, AmountType.COST, false, RC);
            final Trade trade = getTrade(sign, 2, AmountType.COST, true, RC);
            charge.isAffordableFor(player);
            addAmount(sign, 1, charge, RC);
            subtractAmount(sign, 2, trade, RC);
            if (!trade.pay(player)) {
                subtractAmount(sign, 1, charge, RC);
                addAmount(sign, 2, trade, RC);
                throw new ChargeException("Full inventory");
            }
            charge.charge(player);
            Trade.log("Sign", "Trade", "Interact", sign.getLine(3), charge, username, trade, sign.getBlock().getLocation(), RC);
        }
        sign.updateSign();
        return true;
    }

    private Trade rechargeSign(final InterfaceSign sign, final InterfaceRefineCraft RC, final User player) throws SignException, ChargeException {
        final Trade trade = getTrade(sign, 2, AmountType.COST, false, RC);
        if (trade.getItemStack() != null && player.getBase().getItemInHand() != null && trade.getItemStack().getType() == player.getBase().getItemInHand().getType() && trade.getItemStack().getDurability() == player.getBase().getItemInHand().getDurability() && trade.getItemStack().getEnchantments().equals(player.getBase().getItemInHand().getEnchantments())) {
            int amount = player.getBase().getItemInHand().getAmount();
            amount -= amount % trade.getItemStack().getAmount();
            if (amount > 0) {
                final ItemStack stack = player.getBase().getItemInHand().clone();
                stack.setAmount(amount);
                final Trade store = new Trade(stack, RC);
                addAmount(sign, 2, store, RC);
                store.charge(player);
                return store;
            }
        }
        return null;
    }

    @Override
    protected boolean onSignBreak(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, MaxMoneyException {
        final String signOwner = sign.getLine(3);

        final boolean isOwner = (signOwner.length() > 3 && signOwner.substring(2).equalsIgnoreCase(username));
        final boolean canBreak = isOwner ? true : player.isAuthorized("refinecraft.signs.trade.override");
        final boolean canCollect = isOwner ? true : player.isAuthorized("refinecraft.signs.trade.override.collect");

        if (canBreak) {
            try {
                final Trade stored1 = getTrade(sign, 1, AmountType.TOTAL, false, RC);
                final Trade stored2 = getTrade(sign, 2, AmountType.TOTAL, false, RC);

                if (!canCollect) {
                    Trade.log("Sign", "Trade", "Destroy", signOwner, stored2, username, stored1, sign.getBlock().getLocation(), RC);
                    return true;
                }

                final Map<Integer, ItemStack> withdraw1 = stored1.pay(player, OverflowType.RETURN);
                final Map<Integer, ItemStack> withdraw2 = stored2.pay(player, OverflowType.RETURN);

                if (withdraw1 == null && withdraw2 == null) {
                    Trade.log("Sign", "Trade", "Break", signOwner, stored2, username, stored1, sign.getBlock().getLocation(), RC);
                    return true;
                }

                setAmount(sign, 1, BigDecimal.valueOf(withdraw1 == null ? 0L : withdraw1.get(0).getAmount()), RC);
                Trade.log("Sign", "Trade", "Withdraw", signOwner, stored1, username, withdraw1 == null ? null : new Trade(withdraw1.get(0), RC), sign.getBlock().getLocation(), RC);

                setAmount(sign, 2, BigDecimal.valueOf(withdraw2 == null ? 0L : withdraw2.get(0).getAmount()), RC);
                Trade.log("Sign", "Trade", "Withdraw", signOwner, stored2, username, withdraw2 == null ? null : new Trade(withdraw2.get(0), RC), sign.getBlock().getLocation(), RC);

                sign.updateSign();
            } catch (SignException e) {
                if (player.isAuthorized("refinecraft.signs.trade.override")) {
                    return true;
                }
                throw e;
            }
            return false;
        } else {
            return false;
        }
    }

    protected final void validateTrade(final InterfaceSign sign, final int index, final boolean amountNeeded, final InterfaceRefineCraft RC) throws SignException {
        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 1 && !amountNeeded) {
            final BigDecimal money = getMoney(split[0]);
            if (money != null) {
                if (NumberUtil.shortCurrency(money, RC).length() * 2 > 15) {
                    throw new SignException("Line can be too long!");
                }
                sign.setLine(index, NumberUtil.shortCurrency(money, RC) + ":0");
                return;
            }
        }

        if (split.length == 2 && amountNeeded) {
            final BigDecimal money = getMoney(split[0]);
            BigDecimal amount = getBigDecimalPositive(split[1]);
            if (money != null && amount != null) {
                amount = amount.subtract(amount.remainder(money));
                if (amount.compareTo(MINTRANSACTION) < 0 || money.compareTo(MINTRANSACTION) < 0) {
                    throw new SignException(tl("moreThanZero"));
                }
                sign.setLine(index, NumberUtil.shortCurrency(money, RC) + ":" + NumberUtil.shortCurrency(amount, RC).substring(1));
                return;
            }
        }

        if (split.length == 2 && !amountNeeded) {
            final int amount = getIntegerPositive(split[0]);

            if (amount < 1) {
                throw new SignException(tl("moreThanZero"));
            }
            if (!(split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) && getItemStack(split[1], amount, RC).getType() == Material.AIR) {
                throw new SignException(tl("moreThanZero"));
            }
            String newline = amount + " " + split[1] + ":0";
            if ((newline + amount).length() > 15) {
                throw new SignException("Line can be too long!");
            }
            sign.setLine(index, newline);
            return;
        }

        if (split.length == 3 && amountNeeded) {
            final int stackamount = getIntegerPositive(split[0]);
            int amount = getIntegerPositive(split[2]);
            amount -= amount % stackamount;
            if (amount < 1 || stackamount < 1) {
                throw new SignException(tl("moreThanZero"));
            }
            if (!(split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) && getItemStack(split[1], stackamount, RC).getType() == Material.AIR) {
                throw new SignException(tl("moreThanZero"));
            }
            sign.setLine(index, stackamount + " " + split[1] + ":" + amount);
            return;
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    protected final Trade getTrade(final InterfaceSign sign, final int index, final AmountType amountType, final boolean notEmpty, final InterfaceRefineCraft RC) throws SignException {
        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 2) {
            try {
                final BigDecimal money = getMoney(split[0]);
                final BigDecimal amount = notEmpty ? getBigDecimalPositive(split[1]) : getBigDecimal(split[1]);
                if (money != null && amount != null) {
                    return new Trade(amountType == AmountType.COST ? money : amount, RC);
                }
            } catch (SignException e) {
                throw new SignException(tl("tradeSignEmpty"), e);
            }
        }

        if (split.length == 3) {
            if (split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) {
                final int stackamount = getIntegerPositive(split[0]);
                int amount = getInteger(split[2]);
                if (amountType == AmountType.ROUNDED) {
                    amount -= amount % stackamount;
                }
                if (notEmpty && (amount < 1 || stackamount < 1)) {
                    throw new SignException(tl("tradeSignEmpty"));
                }
                return new Trade((amountType == AmountType.COST ? stackamount : amount), RC);
            } else {
                final int stackamount = getIntegerPositive(split[0]);
                final ItemStack item = getItemStack(split[1], stackamount, RC);
                int amount = getInteger(split[2]);
                if (amountType == AmountType.ROUNDED) {
                    amount -= amount % stackamount;
                }
                if (notEmpty && (amount < 1 || stackamount < 1 || item.getType() == Material.AIR || amount < stackamount)) {
                    throw new SignException(tl("tradeSignEmpty"));
                }
                item.setAmount(amountType == AmountType.COST ? stackamount : amount);
                return new Trade(item, RC);
            }
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    protected final void subtractAmount(final InterfaceSign sign, final int index, final Trade trade, final InterfaceRefineCraft RC) throws SignException {
        final BigDecimal money = trade.getMoney();
        if (money != null) {
            changeAmount(sign, index, money.negate(), RC);
        }
        final ItemStack item = trade.getItemStack();
        if (item != null) {
            changeAmount(sign, index, BigDecimal.valueOf(-item.getAmount()), RC);
        }
        final Integer exp = trade.getExperience();
        if (exp != null) {
            changeAmount(sign, index, BigDecimal.valueOf(-exp.intValue()), RC);
        }
    }

    protected final void addAmount(final InterfaceSign sign, final int index, final Trade trade, final InterfaceRefineCraft RC) throws SignException {
        final BigDecimal money = trade.getMoney();
        if (money != null) {
            changeAmount(sign, index, money, RC);
        }
        final ItemStack item = trade.getItemStack();
        if (item != null) {
            changeAmount(sign, index, BigDecimal.valueOf(item.getAmount()), RC);
        }
        final Integer exp = trade.getExperience();
        if (exp != null) {
            changeAmount(sign, index, BigDecimal.valueOf(exp.intValue()), RC);
        }
    }

    //TODO: Translate these exceptions.
    private void changeAmount(final InterfaceSign sign, final int index, final BigDecimal value, final InterfaceRefineCraft RC) throws SignException {
        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 2) {
            final BigDecimal amount = getBigDecimal(split[1]).add(value);
            setAmount(sign, index, amount, RC);
            return;
        }
        if (split.length == 3) {
            final BigDecimal amount = getBigDecimal(split[2]).add(value);
            setAmount(sign, index, amount, RC);
            return;
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }

    //TODO: Translate these exceptions.
    private void setAmount(final InterfaceSign sign, final int index, final BigDecimal value, final InterfaceRefineCraft RC) throws SignException {

        final String line = sign.getLine(index).trim();
        if (line.isEmpty()) {
            throw new SignException("Empty line");
        }
        final String[] split = line.split("[ :]+");

        if (split.length == 2) {
            final BigDecimal money = getMoney(split[0]);
            final BigDecimal amount = getBigDecimal(split[1]);
            if (money != null && amount != null) {
                final String newline = NumberUtil.shortCurrency(money, RC) + ":" + NumberUtil.shortCurrency(value, RC).substring(1);
                if (newline.length() > 15) {
                    throw new SignException("This sign is full: Line too long!");
                }
                sign.setLine(index, newline);
                return;
            }
        }

        if (split.length == 3) {
            if (split[1].equalsIgnoreCase("exp") || split[1].equalsIgnoreCase("xp")) {
                final int stackamount = getIntegerPositive(split[0]);
                final String newline = stackamount + " " + split[1] + ":" + (value.intValueExact());
                if (newline.length() > 15) {
                    throw new SignException("This sign is full: Line too long!");
                }
                sign.setLine(index, newline);
                return;
            } else {
                final int stackamount = getIntegerPositive(split[0]);
                getItemStack(split[1], stackamount, RC);
                final String newline = stackamount + " " + split[1] + ":" + (value.intValueExact());
                if (newline.length() > 15) {
                    throw new SignException("This sign is full: Line too long!");
                }
                sign.setLine(index, newline);
                return;
            }
        }
        throw new SignException(tl("invalidSignLine", index + 1));
    }


    public enum AmountType {
        TOTAL,
        ROUNDED,
        COST
    }
}
