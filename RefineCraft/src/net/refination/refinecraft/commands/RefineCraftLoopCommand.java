package net.refination.refinecraft.commands;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import net.refination.api.MaxMoneyException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.UUID;


public abstract class RefineCraftLoopCommand extends RefineCraftCommand {
    public RefineCraftLoopCommand(String command) {
        super(command);
    }

    protected void loopOfflinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, boolean matchWildcards, final String searchTerm, final String[] commandArgs) throws PlayerNotFoundException, NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException {
        if (searchTerm.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        if (matchWildcards && searchTerm.contentEquals("**")) {
            for (UUID sUser : RC.getUserMap().getAllUniqueUsers()) {
                final User matchedUser = RC.getUser(sUser);
                updatePlayer(server, sender, matchedUser, commandArgs);
            }
        } else if (matchWildcards && searchTerm.contentEquals("*")) {
            boolean skipHidden = sender.isPlayer() && !RC.getUser(sender.getPlayer()).canInteractGhosted();
            for (User onlineUser : RC.getOnlineUsers()) {
                if (skipHidden && onlineUser.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(onlineUser.getBase())) {
                    continue;
                }
                updatePlayer(server, sender, onlineUser, commandArgs);
            }
        } else if (multipleStringMatches) {
            if (searchTerm.trim().length() < 3) {
                throw new PlayerNotFoundException();
            }
            final List<Player> matchedPlayers = server.matchPlayer(searchTerm);
            if (matchedPlayers.isEmpty()) {
                final User matchedUser = getPlayer(server, searchTerm, true, true);
                updatePlayer(server, sender, matchedUser, commandArgs);
            }
            for (Player matchPlayer : matchedPlayers) {
                final User matchedUser = RC.getUser(matchPlayer);
                updatePlayer(server, sender, matchedUser, commandArgs);
            }
        } else {
            final User user = getPlayer(server, searchTerm, true, true);
            updatePlayer(server, sender, user, commandArgs);
        }
    }

    protected void loopOnlinePlayers(final Server server, final CommandSource sender, final boolean multipleStringMatches, boolean matchWildcards, final String searchTerm, final String[] commandArgs) throws PlayerNotFoundException, NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException {
        if (searchTerm.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        boolean skipHidden = sender.isPlayer() && !RC.getUser(sender.getPlayer()).canInteractGhosted();

        if (matchWildcards && (searchTerm.contentEquals("**") || searchTerm.contentEquals("*"))) {
            for (User onlineUser : RC.getOnlineUsers()) {
                if (skipHidden && onlineUser.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(onlineUser.getBase())) {
                    continue;
                }
                updatePlayer(server, sender, onlineUser, commandArgs);
            }
        } else if (multipleStringMatches) {
            if (searchTerm.trim().length() < 2) {
                throw new PlayerNotFoundException();
            }
            boolean foundUser = false;
            final List<Player> matchedPlayers = server.matchPlayer(searchTerm);

            if (matchedPlayers.isEmpty()) {
                final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
                for (User player : RC.getOnlineUsers()) {
                    if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(player.getBase())) {
                        continue;
                    }
                    final String displayName = FormatUtil.stripFormat(player.getDisplayName()).toLowerCase(Locale.ENGLISH);
                    if (displayName.contains(matchText)) {
                        foundUser = true;
                        updatePlayer(server, sender, player, commandArgs);
                    }
                }
            } else {
                for (Player matchPlayer : matchedPlayers) {
                    final User player = RC.getUser(matchPlayer);
                    if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(matchPlayer)) {
                        continue;
                    }
                    foundUser = true;
                    updatePlayer(server, sender, player, commandArgs);
                }
            }
            if (!foundUser) {
                throw new PlayerNotFoundException();
            }
        } else {
            final User player = getPlayer(server, sender, searchTerm);
            updatePlayer(server, sender, player, commandArgs);
        }
    }

    protected abstract void updatePlayer(Server server, CommandSource sender, User user, String[] args) throws NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException;
}
