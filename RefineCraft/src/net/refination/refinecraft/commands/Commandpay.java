package net.refination.refinecraft.commands;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.NumberUtil;

import net.refination.api.MaxMoneyException;
import org.bukkit.Server;

import java.math.BigDecimal;

import static net.refination.refinecraft.I18n.tl;


public class Commandpay extends RefineCraftLoopCommand {
    BigDecimal amount;

    public Commandpay() {
        super("pay");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        String stringAmount = args[1].replaceAll("[^0-9\\.]", "");

        if (stringAmount.length() < 1) {
            throw new NotEnoughArgumentsException();
        }

        amount = new BigDecimal(stringAmount);
        if (amount.compareTo(RC.getSettings().getMinimumPayAmount()) < 0) { // Check if amount is less than minimum-pay-amount
            throw new Exception(tl("minimumPayAmount", NumberUtil.displayCurrencyExactly(RC.getSettings().getMinimumPayAmount(), RC)));
        }
        loopOnlinePlayers(server, user.getSource(), false, user.isAuthorized("refinecraft.pay.multiple"), args[0], args);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws ChargeException {
        User user = RC.getUser(sender.getPlayer());
        try {
            user.payUser(player, amount);
            Trade.log("Command", "Pay", "Player", user.getName(), new Trade(amount, RC), player.getName(), new Trade(amount, RC), user.getLocation(), RC);
        } catch (MaxMoneyException ex) {
            sender.sendMessage(tl("maxMoney"));
            try {
                user.setMoney(user.getMoney().add(amount));
            } catch (MaxMoneyException ignored) {
                // this should never happen
            }
        }
    }
}
