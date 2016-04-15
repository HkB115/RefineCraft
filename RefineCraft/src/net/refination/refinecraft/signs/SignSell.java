package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.Trade.OverflowType;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import net.refination.api.MaxMoneyException;


public class SignSell extends RefineCraftSign {
    public SignSell() {
        super("Sell");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 1, 2, player, RC);
        validateTrade(sign, 3, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException, MaxMoneyException {
        final Trade charge = getTrade(sign, 1, 2, player, RC);
        final Trade money = getTrade(sign, 3, RC);
        charge.isAffordableFor(player);
        money.pay(player, OverflowType.DROP);
        charge.charge(player);
        Trade.log("Sign", "Sell", "Interact", username, charge, username, money, sign.getBlock().getLocation(), RC);
        return true;
    }
}
