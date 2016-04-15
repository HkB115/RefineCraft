package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandgamemode extends RefineCraftCommand {
    public Commandgamemode() {
        super("gamemode");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        GameMode gameMode;
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        } else if (args.length == 1) {
            gameMode = matchGameMode(commandLabel);
            gamemodeOtherPlayers(server, sender, gameMode, args[0]);
        } else if (args.length == 2) {
            gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
            gamemodeOtherPlayers(server, sender, gameMode, args[1]);
        }

    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        GameMode gameMode;
        if (args.length == 0) {
            gameMode = matchGameMode(commandLabel);
        } else if (args.length > 1 && args[1].trim().length() > 2 && user.isAuthorized("refinecraft.gamemode.others")) {
            gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
            gamemodeOtherPlayers(server, user.getSource(), gameMode, args[1]);
            return;
        } else {
            try {
                gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));
            } catch (NotEnoughArgumentsException e) {
                if (user.isAuthorized("refinecraft.gamemode.others")) {
                    gameMode = matchGameMode(commandLabel);
                    gamemodeOtherPlayers(server, user.getSource(), gameMode, args[0]);
                    return;
                }
                throw new NotEnoughArgumentsException();
            }
        }

        if (gameMode == null) {
            gameMode = user.getBase().getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : user.getBase().getGameMode() == GameMode.CREATIVE ? GameMode.ADVENTURE : GameMode.SURVIVAL;
        }

        if (!canChangeToMode(user.getBase(), gameMode)) {
            user.sendMessage(tl("cantGamemode", gameMode.name()));
            return;
        }

        user.getBase().setGameMode(gameMode);
        user.sendMessage(tl("gameMode", tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getDisplayName()));
    }

    private void gamemodeOtherPlayers(final Server server, final CommandSource sender, final GameMode gameMode, final String name) throws NotEnoughArgumentsException, PlayerNotFoundException {
        if (name.trim().length() < 2 || gameMode == null) {
            throw new NotEnoughArgumentsException(tl("gameModeInvalid"));
        }

        if (sender.isPlayer() && !canChangeToMode(sender.getPlayer(), gameMode)) {
            sender.sendMessage(tl("cantGamemode", gameMode.name()));
            return;
        }

        boolean skipHidden = sender.isPlayer() && !RC.getUser(sender.getPlayer()).canInteractGhosted();
        boolean foundUser = false;
        final List<Player> matchedPlayers = server.matchPlayer(name);
        for (Player matchPlayer : matchedPlayers) {
            final User player = RC.getUser(matchPlayer);
            if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(matchPlayer)) {
                continue;
            }
            foundUser = true;
            player.getBase().setGameMode(gameMode);
            sender.sendMessage(tl("gameMode", tl(player.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }

    // refinecraft.gamemode will let them change to any but refinecraft.gamemode.survival would only let them change to survival.
    private boolean canChangeToMode(Player player, GameMode to) {
        return player.hasPermission("refinecraft.gamemode.all") || player.hasPermission("refinecraft.gamemode." + to.name().toLowerCase());
    }

    private GameMode matchGameMode(String modeString) throws NotEnoughArgumentsException {
        GameMode mode = null;
        if (modeString.equalsIgnoreCase("gmc") || modeString.equalsIgnoreCase("egmc") || modeString.contains("creat") || modeString.equalsIgnoreCase("1") || modeString.equalsIgnoreCase("c")) {
            mode = GameMode.CREATIVE;
        } else if (modeString.equalsIgnoreCase("gms") || modeString.equalsIgnoreCase("egms") || modeString.contains("survi") || modeString.equalsIgnoreCase("0") || modeString.equalsIgnoreCase("s")) {
            mode = GameMode.SURVIVAL;
        } else if (modeString.equalsIgnoreCase("gma") || modeString.equalsIgnoreCase("egma") || modeString.contains("advent") || modeString.equalsIgnoreCase("2") || modeString.equalsIgnoreCase("a")) {
            mode = GameMode.ADVENTURE;
        } else if (modeString.equalsIgnoreCase("gmt") || modeString.equalsIgnoreCase("egmt") || modeString.contains("toggle") || modeString.contains("cycle") || modeString.equalsIgnoreCase("t")) {
            mode = null;
        } else if (modeString.equalsIgnoreCase("gmsp") || modeString.equalsIgnoreCase("egmsp") || modeString.contains("spec") || modeString.equalsIgnoreCase("3") || modeString.equalsIgnoreCase("sp")) {
            mode = GameMode.SPECTATOR;
        } else {
            throw new NotEnoughArgumentsException();
        }
        return mode;
    }
}