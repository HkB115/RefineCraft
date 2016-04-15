package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import net.refination.api.events.NickChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandnick extends RefineCraftLoopCommand {
    public Commandnick() {
        super("nick");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        if (!RC.getSettings().changeDisplayName()) {
            throw new Exception(tl("nickDisplayName"));
        }

        if (args.length > 1 && user.isAuthorized("refinecraft.nick.others")) {
            final String[] nickname = formatNickname(user, args[1]).split(" ");
            loopOfflinePlayers(server, user.getSource(), false, true, args[0], nickname);
            user.sendMessage(tl("nickChanged"));
        } else {
            final String[] nickname = formatNickname(user, args[0]).split(" ");
            updatePlayer(server, user.getSource(), user, nickname);
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        if (!RC.getSettings().changeDisplayName()) {
            throw new Exception(tl("nickDisplayName"));
        }
        final String[] nickname = formatNickname(null, args[1]).split(" ");
        loopOfflinePlayers(server, sender, false, true, args[0], nickname);
        sender.sendMessage(tl("nickChanged"));
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User target, final String[] args) throws NotEnoughArgumentsException {
        final String nick = args[0];
        if (target.getName().equalsIgnoreCase(nick)) {
            String oldName = target.getDisplayName();
            setNickname(server, sender, target, nick);
            if (!target.getDisplayName().equalsIgnoreCase(oldName)) {
                target.sendMessage(tl("nickNoMore"));
            }
            target.sendMessage(tl("nickSet", target.getDisplayName()));
        } else if ("off".equalsIgnoreCase(nick)) {
            setNickname(server, sender, target, null);
            target.sendMessage(tl("nickNoMore"));
        } else if (nickInUse(server, target, nick)) {
            throw new NotEnoughArgumentsException(tl("nickInUse"));
        } else {
            setNickname(server, sender, target, nick);
            target.sendMessage(tl("nickSet", target.getDisplayName()));
        }
    }

    private String formatNickname(final User user, final String nick) throws Exception {
        String newNick = user == null ? FormatUtil.replaceFormat(nick) : FormatUtil.formatString(user, "refinecraft.nick", nick);
        if (!newNick.matches("^[a-zA-Z_0-9\u00a7]+$")) {
            throw new Exception(tl("nickNamesAlpha"));
        } else if (getNickLength(newNick) > RC.getSettings().getMaxNickLength()) {
            throw new Exception(tl("nickTooLong"));
        } else if (FormatUtil.stripFormat(newNick).length() < 1) {
            throw new Exception(tl("nickNamesAlpha"));
        } else if (user != null && (user.isAuthorized("refinecraft.nick.changecolors") && !user.isAuthorized("refinecraft.nick.changecolors.bypass")) && !FormatUtil.stripFormat(newNick).equals(user.getName())) {
            throw new Exception(tl("nickNamesOnlyColorChanges"));
        }
        return newNick;
    }

    private int getNickLength(final String nick) {
        return RC.getSettings().ignoreColorsInMaxLength() ? ChatColor.stripColor(nick).length() : nick.length();
    }

    private boolean nickInUse(final Server server, final User target, String nick) {
        final String lowerNick = FormatUtil.stripFormat(nick.toLowerCase(Locale.ENGLISH));
        for (final Player onlinePlayer : RC.getOnlinePlayers()) {
            if (target.getBase().getName().equals(onlinePlayer.getName())) {
                continue;
            }
            final String matchNick = FormatUtil.stripFormat(onlinePlayer.getDisplayName().replace(RC.getSettings().getNicknamePrefix(), ""));
            if (lowerNick.equals(matchNick.toLowerCase(Locale.ENGLISH)) || lowerNick.equals(onlinePlayer.getName().toLowerCase(Locale.ENGLISH))) {
                return true;
            }
        }
        return RC.getUser(lowerNick) != null && RC.getUser(lowerNick) != target;
    }

    private void setNickname(final Server server, final CommandSource sender, final User target, final String nickname) {
        final User controller = sender.isPlayer() ? RC.getUser(sender.getPlayer()) : null;
        final NickChangeEvent nickEvent = new NickChangeEvent(controller, target, nickname);
        server.getPluginManager().callEvent(nickEvent);
        if (!nickEvent.isCancelled()) {
            target.setNickname(nickname);
            target.setDisplayNick();
        }
    }
}
