package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.commands.Commandrepair;
import net.refination.refinecraft.commands.NotEnoughArgumentsException;
import net.refination.api.InterfaceRefineCraft;

import static net.refination.refinecraft.I18n.tl;


public class SignRepair extends RefineCraftSign {
    public SignRepair() {
        super("Repair");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        final String repairTarget = sign.getLine(1);
        if (repairTarget.isEmpty()) {
            sign.setLine(1, "Hand");
        } else if (!repairTarget.equalsIgnoreCase("all") && !repairTarget.equalsIgnoreCase("hand")) {
            sign.setLine(1, "§c<hand|all>");
            throw new SignException(tl("invalidSignLine", 2));
        }
        validateTrade(sign, 2, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, RC);
        charge.isAffordableFor(player);

        Commandrepair command = new Commandrepair();
        command.setRefineCraft(RC);

        try {
            if (sign.getLine(1).equalsIgnoreCase("hand")) {
                command.repairHand(player);
            } else if (sign.getLine(1).equalsIgnoreCase("all")) {
                command.repairAll(player);
            } else {
                throw new NotEnoughArgumentsException();
            }

        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        charge.charge(player);
        Trade.log("Sign", "Repair", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
        return true;
    }
}
