package net.refination.refinecraft.chat;

import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

import java.util.Locale;
import java.util.Map;


public class RCchatPlayerListenerLowest extends RCchatPlayer {
    public RCchatPlayerListenerLowest(final Server server, final InterfaceRefineCraft RC, final Map<AsyncPlayerChatEvent, ChatStore> chatStorage) {
        super(server, RC, chatStorage);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Override
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        final User user = RC.getUser(event.getPlayer());

        if (user == null) {
            event.setCancelled(true);
            return;
        }

        final ChatStore chatStore = new ChatStore(RC, user, getChatType(event.getMessage()));
        setChatStore(event, chatStore);

        /**
         * This listener should apply the general chat formatting only...then return control back the event handler
         */
        event.setMessage(FormatUtil.formatMessage(user, "RCentials.chat", event.getMessage()));
        String group = user.getGroup();
        String world = user.getWorld().getName();
        Team team = user.getBase().getScoreboard().getPlayerTeam(user.getBase());

        String format = RC.getSettings().getChatFormat(group);
        format = format.replace("{0}", group);
        format = format.replace("{1}", world);
        format = format.replace("{2}", world.substring(0, 1).toUpperCase(Locale.ENGLISH));
        format = format.replace("{3}", team == null ? "" : team.getPrefix());
        format = format.replace("{4}", team == null ? "" : team.getSuffix());
        format = format.replace("{5}", team == null ? "" : team.getDisplayName());
        synchronized (format) {
            event.setFormat(format);
        }
    }
}
