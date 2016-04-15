package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;


public abstract class RefineCraftToggleCommand extends RefineCraftCommand {
    String othersPermission;

    public RefineCraftToggleCommand(String command, String othersPermission) {
        super(command);
        this.othersPermission = othersPermission;
    }

    protected void handleToggleWithArgs(Server server, User user, String[] args) throws Exception {
        if (args.length == 1) {
            Boolean toggle = matchToggleArgument(args[0]);
            if (toggle == null && user.isAuthorized(othersPermission)) {
                toggleOtherPlayers(server, user.getSource(), args);
            } else {
                togglePlayer(user.getSource(), user, toggle);
            }
        } else if (args.length == 2 && user.isAuthorized(othersPermission)) {
            toggleOtherPlayers(server, user.getSource(), args);
        } else {
            togglePlayer(user.getSource(), user, null);
        }
    }

    protected Boolean matchToggleArgument(final String arg) {
        if (arg.equalsIgnoreCase("on") || arg.startsWith("ena") || arg.equalsIgnoreCase("1")) {
            return true;
        } else if (arg.equalsIgnoreCase("off") || arg.startsWith("dis") || arg.equalsIgnoreCase("0")) {
            return false;
        }
        return null;
    }

    protected void toggleOtherPlayers(final Server server, final CommandSource sender, final String[] args) throws PlayerNotFoundException, NotEnoughArgumentsException {
        if (args.length < 1 || args[0].trim().length() < 2) {
            throw new PlayerNotFoundException();
        }

        boolean skipHidden = sender.isPlayer() && !RC.getUser(sender.getPlayer()).canInteractGhosted();
        boolean foundUser = false;
        final List<Player> matchedPlayers = server.matchPlayer(args[0]);
        for (Player matchPlayer : matchedPlayers) {
            final User player = RC.getUser(matchPlayer);
            if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(matchPlayer)) {
                continue;
            }
            foundUser = true;
            if (args.length > 1) {
                Boolean toggle = matchToggleArgument(args[1]);
                if (toggle == true) {
                    togglePlayer(sender, player, true);
                } else {
                    togglePlayer(sender, player, false);
                }
            } else {
                togglePlayer(sender, player, null);
            }
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }

    // Make sure when implementing this method that all 3 Boolean states are handled, 'null' should toggle the existing state.
    abstract void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException;
}
