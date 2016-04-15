package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.SpawnMob;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;

import java.util.List;


public class SignSpawnmob extends RefineCraftSign {
    public SignSpawnmob() {
        super("Spawnmob");
    }

    @Override
    protected boolean onSignCreate(InterfaceSign sign, User player, String username, InterfaceRefineCraft RC) throws SignException, ChargeException {
        validateInteger(sign, 1);
        validateTrade(sign, 3, RC);
        return true;
    }

    @Override
    protected boolean onSignInteract(InterfaceSign sign, User player, String username, InterfaceRefineCraft RC) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 3, RC);
        charge.isAffordableFor(player);

        try {
            List<String> mobParts = SpawnMob.mobParts(sign.getLine(2));
            List<String> mobData = SpawnMob.mobData(sign.getLine(2));
            SpawnMob.spawnmob(RC, RC.getServer(), player.getSource(), player, mobParts, mobData, Integer.parseInt(sign.getLine(1)));
        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        charge.charge(player);
        Trade.log("Sign", "Spawnmob", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
        return true;
    }
}
