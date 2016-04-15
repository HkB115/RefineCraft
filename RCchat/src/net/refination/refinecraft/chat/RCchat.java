package net.refination.refinecraft.chat;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class RCchat extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        final PluginManager pluginManager = getServer().getPluginManager();
        final InterfaceRefineCraft RC = (InterfaceRefineCraft) pluginManager.getPlugin("RefineCraft");
        if (!this.getDescription().getVersion().equals(RC.getDescription().getVersion())) {
            LOGGER.log(Level.WARNING, tl("versionMismatchAll"));
        }
        if (!RC.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        final Map<AsyncPlayerChatEvent, ChatStore> chatStore = Collections.synchronizedMap(new HashMap<AsyncPlayerChatEvent, ChatStore>());

        final RCchatPlayerListenerLowest playerListenerLowest = new RCchatPlayerListenerLowest(getServer(), RC, chatStore);
        final RCchatPlayerListenerNormal playerListenerNormal = new RCchatPlayerListenerNormal(getServer(), RC, chatStore);
        final RCchatPlayerListenerHighest playerListenerHighest = new RCchatPlayerListenerHighest(getServer(), RC, chatStore);
        pluginManager.registerEvents(playerListenerLowest, this);
        pluginManager.registerEvents(playerListenerNormal, this);
        pluginManager.registerEvents(playerListenerHighest, this);

    }
}
