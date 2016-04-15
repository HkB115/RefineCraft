package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;

import static net.refination.refinecraft.I18n.tl;


public class SignHeal extends RefineCraftSign {
    public SignHeal() {
        super("Heal");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 1, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        if (player.getBase().getHealth() == 0) {
            throw new SignException(tl("healDead"));
        }
        final Trade charge = getTrade(sign, 1, RC);
        charge.isAffordableFor(player);
        player.getBase().setHealth(20);
        player.getBase().setFoodLevel(20);
        player.getBase().setFireTicks(0);
        player.sendMessage(tl("youAreHealed"));
        charge.charge(player);
        Trade.log("Sign", "Heal", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
        return true;
    }
}
