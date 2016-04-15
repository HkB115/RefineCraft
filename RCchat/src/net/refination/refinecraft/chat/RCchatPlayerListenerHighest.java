package net.refination.refinecraft.chat;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;


public class RCchatPlayerListenerHighest extends RCchatPlayer {
    public RCchatPlayerListenerHighest(final Server server, final InterfaceRefineCraft RC, final Map<AsyncPlayerChatEvent, ChatStore> chatStorage) {
        super(server, RC, chatStorage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Override
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final ChatStore chatStore = delChatStore(event);
        if (isAborted(event) || chatStore == null) {
            return;
        }

        /**
         * This file should handle charging the user for the action before returning control back
         */
        charge(event, chatStore);
    }
}
