package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Kit;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.commands.NoChargeException;
import net.refination.api.InterfaceRefineCraft;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class SignKit extends RefineCraftSign {
    public SignKit() {
        super("Kit");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 3, RC);

        final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH).trim();

        if (kitName.isEmpty()) {
            sign.setLine(1, "§dKit name!");
            return false;
        } else {
            try {
                RC.getSettings().getKit(kitName);
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            final String group = sign.getLine(2);
            if ("Everyone".equalsIgnoreCase(group) || "Everybody".equalsIgnoreCase(group)) {
                sign.setLine(2, "§2Everyone");
            }
            return true;
        }
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH).trim();
        final String group = sign.getLine(2).trim();
        if ((!group.isEmpty() && ("§2Everyone".equals(group) || player.inGroup(group))) || (group.isEmpty() && (player.isAuthorized("refinecraft.kits." + kitName)))) {
            final Trade charge = getTrade(sign, 3, RC);
            charge.isAffordableFor(player);
            try {
                final Kit kit = new Kit(kitName, RC);
                kit.checkDelay(player);
                kit.setTime(player);
                kit.expandItems(player);

                charge.charge(player);
                Trade.log("Sign", "Kit", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
            } catch (NoChargeException ex) {
                return false;
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            return true;
        } else {
            if (group.isEmpty()) {
                throw new SignException(tl("noKitPermission", "refinecraft.kits." + kitName));
            } else {
                throw new SignException(tl("noKitGroup", group));
            }
        }
    }
}
