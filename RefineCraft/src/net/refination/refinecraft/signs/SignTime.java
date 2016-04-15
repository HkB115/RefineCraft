package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;

import static net.refination.refinecraft.I18n.tl;


public class SignTime extends RefineCraftSign {
    public SignTime() {
        super("Time");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 2, RC);
        final String timeString = sign.getLine(1);
        if ("Day".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Day");
            return true;
        }
        if ("Night".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Night");
            return true;
        }
        throw new SignException(tl("onlyDayNight"));
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, RC);
        charge.isAffordableFor(player);
        final String timeString = sign.getLine(1);
        long time = player.getWorld().getTime();
        time -= time % 24000;
        if ("§2Day".equalsIgnoreCase(timeString)) {
            player.getWorld().setTime(time + 24000);
            charge.charge(player);
            Trade.log("Sign", "TimeDay", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
            return true;
        }
        if ("§2Night".equalsIgnoreCase(timeString)) {
            player.getWorld().setTime(time + 37700);
            charge.charge(player);
            Trade.log("Sign", "TimeNight", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
            return true;
        }
        throw new SignException(tl("onlyDayNight"));
    }
}
