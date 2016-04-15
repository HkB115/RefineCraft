package net.refination.refinecraft.signs;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.NumberUtil;
import net.refination.api.InterfaceRefineCraft;

import static net.refination.refinecraft.I18n.tl;


public class SignBalance extends RefineCraftSign {
    public SignBalance() {
        super("Balance");
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        player.sendMessage(tl("balance", NumberUtil.displayCurrency(player.getMoney(), RC)));
        return true;
    }
}
