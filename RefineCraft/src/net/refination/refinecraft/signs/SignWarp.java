package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static net.refination.refinecraft.I18n.tl;


public class SignWarp extends RefineCraftSign {
    public SignWarp() {
        super("Warp");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        validateTrade(sign, 3, RC);
        final String warpName = sign.getLine(1);

        if (warpName.isEmpty()) {
            sign.setLine(1, "§c<Warp name>");
            throw new SignException(tl("invalidSignLine", 1));
        } else {
            try {
                RC.getWarps().getWarp(warpName);
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
        final String warpName = sign.getLine(1);
        final String group = sign.getLine(2);
        if ((!group.isEmpty() && ("§2Everyone".equals(group) || player.inGroup(group))) || (group.isEmpty() && (!RC.getSettings().getPerWarpPermission() || player.isAuthorized("refinecraft.warps." + warpName)))) {
            final Trade charge = getTrade(sign, 3, RC);
            try {
                player.getTeleport().warp(player, warpName, charge, TeleportCause.PLUGIN);
                Trade.log("Sign", "Warp", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            return true;
        }
        return false;
    }
}
