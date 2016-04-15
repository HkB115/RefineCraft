package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import net.refination.api.MaxMoneyException;


public class SignBuy extends RefineCraftSign {
    public SignBuy() {
        super("Buy");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 1, 2, player, RC);
        validateTrade(sign, 3, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException, MaxMoneyException {
        final Trade items = getTrade(sign, 1, 2, player, RC);
        final Trade charge = getTrade(sign, 3, RC);
        charge.isAffordableFor(player);
        if (!items.pay(player)) {
            throw new ChargeException("Inventory full"); //TODO: TL
        }
        charge.charge(player);
        Trade.log("Sign", "Buy", "Interact", username, charge, username, items, sign.getBlock().getLocation(), RC);
        return true;
    }
}
