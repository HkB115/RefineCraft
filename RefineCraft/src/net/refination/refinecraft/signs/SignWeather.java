package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;

import static net.refination.refinecraft.I18n.tl;


public class SignWeather extends RefineCraftSign {
    public SignWeather() {
        super("Weather");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 2, RC);
        final String timeString = sign.getLine(1);
        if ("Sun".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Sun");
            return true;
        }
        if ("Storm".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Storm");
            return true;
        }
        sign.setLine(1, "§c<sun|storm>");
        throw new SignException(tl("onlySunStorm"));
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, RC);
        charge.isAffordableFor(player);
        final String weatherString = sign.getLine(1);
        if ("§2Sun".equalsIgnoreCase(weatherString)) {
            player.getWorld().setStorm(false);
            charge.charge(player);
            Trade.log("Sign", "WeatherSun", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
            return true;
        }
        if ("§2Storm".equalsIgnoreCase(weatherString)) {
            player.getWorld().setStorm(true);
            charge.charge(player);
            Trade.log("Sign", "WeatherStorm", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
            return true;
        }
        throw new SignException(tl("onlySunStorm"));
    }
}
