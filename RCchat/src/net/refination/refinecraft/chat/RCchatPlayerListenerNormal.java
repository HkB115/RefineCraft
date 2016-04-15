package net.refination.refinecraft.chat;

import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import net.refination.api.events.LocalChatSpyEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class RCchatPlayerListenerNormal extends RCchatPlayer {
    public RCchatPlayerListenerNormal(final Server server, final InterfaceRefineCraft RC, final Map<AsyncPlayerChatEvent, ChatStore> chatStorage) {
        super(server, RC, chatStorage);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    @Override
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        /**
         * This file should handle detection of the local chat features... if local chat is enabled, we need to handle
         * it here
         */
        long radius = RC.getSettings().getChatRadius();
        if (radius < 1) {
            return;
        }
        radius *= radius;

        final ChatStore chatStore = getChatStore(event);
        final User user = chatStore.getUser();
        chatStore.setRadius(radius);

        if (event.getMessage().length() > 1 && chatStore.getType().length() > 0) {
            final StringBuilder permission = new StringBuilder();
            permission.append("RCentials.chat.").append(chatStore.getType());

            if (user.isAuthorized(permission.toString())) {
                event.setMessage(event.getMessage().substring(1));
                event.setFormat(tl(chatStore.getType() + "Format", event.getFormat()));
                return;
            }

            user.sendMessage(tl("notAllowedTo" + chatStore.getType().substring(0, 1).toUpperCase(Locale.ENGLISH) + chatStore.getType().substring(1)));
            event.setCancelled(true);
            return;
        }

        final Location loc = user.getLocation();
        final World world = loc.getWorld();

        if (!charge(event, chatStore)) {
            return;
        }

        Set<Player> outList = event.getRecipients();
        Set<Player> spyList = new HashSet<Player>();

        try {
            outList.add(event.getPlayer());
        } catch (UnsupportedOperationException ex) {
            if (RC.getSettings().isDebug()) {
                RC.getLogger().log(Level.INFO, "Plugin triggered custom chat event, local chat handling aborted.", ex);
            }
            return;
        }

        final String format = event.getFormat();
        event.setFormat(tl("chatTypeLocal").concat(event.getFormat()));

        logger.info(tl("localFormat", user.getName(), event.getMessage()));

        final Iterator<Player> it = outList.iterator();
        while (it.hasNext()) {
            final Player onlinePlayer = it.next();
            final User onlineUser = RC.getUser(onlinePlayer);
            if (!onlineUser.equals(user)) {
                boolean abort = false;
                final Location playerLoc = onlineUser.getLocation();
                if (playerLoc.getWorld() != world) {
                    abort = true;
                } else {
                    final double delta = playerLoc.distanceSquared(loc);
                    if (delta > chatStore.getRadius()) {
                        abort = true;
                    }
                }
                if (abort) {
                    if (onlineUser.isAuthorized("RCentials.chat.spy")) {
                        spyList.add(onlinePlayer);
                    }
                    it.remove();
                }
            }
        }

        if (outList.size() < 2) {
            user.sendMessage(tl("localNoOne"));
        }

        LocalChatSpyEvent spyEvent = new LocalChatSpyEvent(event.isAsynchronous(), event.getPlayer(), format, event.getMessage(), spyList);
        server.getPluginManager().callEvent(spyEvent);

        if (!spyEvent.isCancelled()) {
            for (Player onlinePlayer : spyEvent.getRecipients()) {
                onlinePlayer.sendMessage(String.format(spyEvent.getFormat(), user.getDisplayName(), spyEvent.getMessage()));
            }
        }
    }
}