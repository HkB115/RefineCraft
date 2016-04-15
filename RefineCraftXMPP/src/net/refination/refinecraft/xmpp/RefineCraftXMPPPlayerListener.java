package net.refination.refinecraft.xmpp;

import net.refination.refinecraft.InterfaceRefineCraft;
import net.refination.refinecraft.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;


class RefineCraftXMPPPlayerListener implements Listener {
    private final transient InterfaceRefineCraft RC;

    RefineCraftXMPPPlayerListener(final InterfaceRefineCraft RC) {
        super();
        this.RC = RC;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final User user = RC.getUser(event.getPlayer());

        Bukkit.getScheduler().scheduleSyncDelayedTask(RC, new Runnable() {
            @Override
            public void run() {
                RefineCraftXMPP.updatePresence();
            }
        });

        sendMessageToSpyUsers("Player " + user.getDisplayName() + " joined the game");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final User user = RC.getUser(event.getPlayer());
        sendMessageToSpyUsers(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final User user = RC.getUser(event.getPlayer());

        Bukkit.getScheduler().scheduleSyncDelayedTask(RC, new Runnable() {
            @Override
            public void run() {
                RefineCraftXMPP.updatePresence();
            }
        });


        sendMessageToSpyUsers("Player " + user.getDisplayName() + " left the game");
    }

    private void sendMessageToSpyUsers(final String message) {
        try {
            List<String> users = RefineCraftXMPP.getInstance().getSpyUsers();
            synchronized (users) {
                for (final String address : users) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RC, new Runnable() {
                        @Override
                        public void run() {
                            RefineCraftXMPP.getInstance().sendMessage(address, message);
                        }
                    });

                }
            }
        } catch (Exception ex) {
            // Ignore exceptions
        }
    }
}
