package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.KeywordReplacer;
import net.refination.refinecraft.textreader.TextInput;
import net.refination.refinecraft.textreader.TextPager;
import net.refination.api.InterfaceRefineCraft;

import java.io.IOException;


public class SignInfo extends RefineCraftSign {
    public SignInfo() {
        super("Info");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 3, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 3, RC);
        charge.isAffordableFor(player);

        String chapter = sign.getLine(1);
        String page = sign.getLine(2);

        final InterfaceText input;
        try {
            player.setDisplayNick();
            input = new TextInput(player.getSource(), "info", true, RC);
            final InterfaceText output = new KeywordReplacer(input, player.getSource(), RC);
            final TextPager pager = new TextPager(output);
            pager.showPage(chapter, page, null, player.getSource());

        } catch (IOException ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        charge.charge(player);
        Trade.log("Sign", "Info", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
        return true;
    }
}
