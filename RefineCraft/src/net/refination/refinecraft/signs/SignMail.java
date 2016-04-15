package net.refination.refinecraft.signs;

import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;

import java.util.List;

import static net.refination.refinecraft.I18n.tl;


public class SignMail extends RefineCraftSign {
    public SignMail() {
        super("Mail");
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        final List<String> mail = player.getMails();
        if (mail.isEmpty()) {
            player.sendMessage(tl("noNewMail"));
            return false;
        }
        for (String s : mail) {
            player.sendMessage(s);
        }
        player.sendMessage(tl("markMailAsRead"));
        return true;
    }
}
