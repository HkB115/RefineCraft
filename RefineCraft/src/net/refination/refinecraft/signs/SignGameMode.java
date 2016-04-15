package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class SignGameMode extends RefineCraftSign {
    public SignGameMode() {
        super("GameMode");
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        final String gamemode = sign.getLine(1);
        if (gamemode.isEmpty()) {
            sign.setLine(1, "Survival");
        }

        validateTrade(sign, 2, RC);

        return true;
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, RC);
        final String mode = sign.getLine(1).trim();

        if (mode.isEmpty()) {
            throw new SignException(tl("invalidSignLine", 2));
        }

        charge.isAffordableFor(player);

        performSetMode(mode.toLowerCase(Locale.ENGLISH), player.getBase());
        player.sendMessage(tl("gameMode", tl(player.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
        Trade.log("Sign", "gameMode", "Interact", username, null, username, charge, sign.getBlock().getLocation(), RC);
        charge.charge(player);
        return true;
    }

    private void performSetMode(String mode, Player player) throws SignException {
        if (mode.contains("survi") || mode.equalsIgnoreCase("0")) {
            player.setGameMode(GameMode.SURVIVAL);
        } else if (mode.contains("creat") || mode.equalsIgnoreCase("1")) {
            player.setGameMode(GameMode.CREATIVE);
        } else if (mode.contains("advent") || mode.equalsIgnoreCase("2")) {
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            throw new SignException(tl("invalidSignLine", 2));
        }
    }
}
