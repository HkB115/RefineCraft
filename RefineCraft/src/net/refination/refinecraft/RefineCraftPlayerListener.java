package net.refination.refinecraft;

import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.KeywordReplacer;
import net.refination.refinecraft.textreader.TextInput;
import net.refination.refinecraft.textreader.TextPager;
import net.refination.refinecraft.utils.LocationUtil;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class RefineCraftPlayerListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("RefineCraft");
    private final transient InterfaceRefineCraft RC;

    public RefineCraftPlayerListener(final InterfaceRefineCraft parent) {
        this.RC = parent;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final User user = RC.getUser(event.getPlayer());
        updateCompass(user);
        user.setDisplayNick();

        if (RC.getSettings().isTeleportInvulnerability()) {
            user.enableInvulnerabilityAfterTeleport();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final User user = RC.getUser(event.getPlayer());
        if (user.isMuted()) {
            event.setCancelled(true);
            user.sendMessage(tl("voiceSilenced"));
            LOGGER.info(tl("mutedUserSpeaks", user.getName()));
        }
        try {
            final Iterator<Player> it = event.getRecipients().iterator();
            while (it.hasNext()) {
                final User u = RC.getUser(it.next());
                if (u.isIgnoredPlayer(user)) {
                    it.remove();
                }
            }
        } catch (UnsupportedOperationException ex) {
            if (RC.getSettings().isDebug()) {
                RC.getLogger().log(Level.INFO, "Ignore could not block chat due to custom chat plugin event.", ex);
            } else {
                RC.getLogger().info("Ignore could not block chat due to custom chat plugin event.");
            }
        }

        user.updateActivity(true);
        user.setDisplayNick();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            return;
        }

        if (!RC.getSettings().cancelAfkOnMove() && !RC.getSettings().getFreezeAfkPlayers()) {
            event.getHandlers().unregister(this);

            if (RC.getSettings().isDebug()) {
                LOGGER.log(Level.INFO, "Unregistering move listener");
            }

            return;
        }

        final User user = RC.getUser(event.getPlayer());
        if (user.isAfk() && RC.getSettings().getFreezeAfkPlayers()) {
            final Location from = event.getFrom();
            final Location origTo = event.getTo();
            final Location to = origTo.clone();
            if (RC.getSettings().cancelAfkOnMove() && origTo.getY() >= from.getBlockY() + 1) {
                user.updateActivity(true);
                return;
            }
            to.setX(from.getX());
            to.setY(from.getY());
            to.setZ(from.getZ());
            try {
                event.setTo(LocationUtil.getSafeDestination(to));
            } catch (Exception ex) {
                event.setTo(to);
            }
            return;
        }
        final Location afk = user.getAfkPosition();
        if (afk == null || !event.getTo().getWorld().equals(afk.getWorld()) || afk.distanceSquared(event.getTo()) > 9) {
            user.updateActivity(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final User user = RC.getUser(event.getPlayer());

        if (RC.getSettings().allowSilentJoinQuit() && user.isAuthorized("refinecraft.silentquit")) {
            event.setQuitMessage(null);
        } else if (RC.getSettings().isCustomQuitMessage() && event.getQuitMessage() != null) {
            final Player player = event.getPlayer();
            event.setQuitMessage(RC.getSettings().getCustomQuitMessage().replace("{PLAYER}", player.getDisplayName()).replace("{USERNAME}", player.getName()));
        }

        user.startTransaction();
        if (RC.getSettings().removeGodOnDisconnect() && user.isGodModeEnabled()) {
            user.setGodModeEnabled(false);
        }
        if (user.isGhost()) {
            user.setGhosted(false);
        }
        user.setLogoutLocation();
        if (user.isRecipeSee()) {
            user.getBase().getOpenInventory().getTopInventory().clear();
        }

        for (HumanEntity viewer : user.getBase().getInventory().getViewers()) {
            if (viewer instanceof Player) {
                User uviewer = RC.getUser((Player) viewer);
                if (uviewer.isInvSee()) {
                    uviewer.getBase().closeInventory();
                }
            }
        }

        user.updateActivity(false);
        if (!user.isHidden()) {
            user.setLastLogout(System.currentTimeMillis());
        }
        user.stopTransaction();

        user.dispose();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final String joinMessage = event.getJoinMessage();
        RC.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                delayedJoin(event.getPlayer(), joinMessage);
            }
        });
        if (RC.getSettings().allowSilentJoinQuit() || RC.getSettings().isCustomJoinMessage()) {
            event.setJoinMessage(null);
        }
    }

    public void delayedJoin(final Player player, final String message) {
        if (!player.isOnline()) {
            return;
        }

        RC.getBackup().onPlayerJoin();
        final User dUser = RC.getUser(player);

        dUser.startTransaction();
        if (dUser.isNPC()) {
            dUser.setNPC(false);
        }

        final long currentTime = System.currentTimeMillis();
        dUser.checkMuteTimeout(currentTime);
        dUser.updateActivity(false);
        dUser.stopTransaction();

        InterfaceText tempInput = null;

        if (!RC.getSettings().isCommandDisabled("motd")) {
            try {
                tempInput = new TextInput(dUser.getSource(), "motd", true, RC);
            } catch (IOException ex) {
                if (RC.getSettings().isDebug()) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                } else {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            }
        }

        final InterfaceText input = tempInput;

        class DelayJoinTask implements Runnable {
            @Override
            public void run() {
                final User user = RC.getUser(player);

                if (!user.getBase().isOnline()) {
                    return;
                }

                user.startTransaction();

                user.setLastAccountName(user.getBase().getName());
                user.setLastLogin(currentTime);
                user.setDisplayNick();
                updateCompass(user);

                if (!RC.getGhostedPlayers().isEmpty() && !user.isAuthorized("refinecraft.ghost.see")) {
                    for (String p : RC.getGhostedPlayers()) {
                        Player toGhost = RC.getServer().getPlayerExact(p);
                        if (toGhost != null && toGhost.isOnline()) {
                            user.getBase().hidePlayer(toGhost);
                        }
                    }
                }

                if (user.isAuthorized("refinecraft.sleepingignored")) {
                    user.getBase().setSleepingIgnored(true);
                }

                if (RC.getSettings().allowSilentJoinQuit() && (user.isAuthorized("refinecraft.silentjoin") || user.isAuthorized("refinecraft.silentjoin.ghost"))) {
                    if (user.isAuthorized("refinecraft.silentjoin.ghost")) {
                        user.setGhosted(true);
                    }
                } else if (message == null) {
                    //NOOP
                } else if (RC.getSettings().isCustomJoinMessage()) {
                    String msg = RC.getSettings().getCustomJoinMessage().replace("{PLAYER}", player.getDisplayName()).replace("{USERNAME}", player.getName()).replace("{UNIQUE}", String.valueOf(RC.getUserMap().getUniqueUsers()));
                    RC.getServer().broadcastMessage(msg);
                } else if (RC.getSettings().allowSilentJoinQuit()) {
                    RC.getServer().broadcastMessage(message);
                }

                if (input != null && user.isAuthorized("refinecraft.motd")) {
                    final InterfaceText output = new KeywordReplacer(input, user.getSource(), RC);
                    final TextPager pager = new TextPager(output, true);
                    pager.showPage("1", null, "motd", user.getSource());
                }

                if (!RC.getSettings().isCommandDisabled("mail") && user.isAuthorized("refinecraft.mail")) {
                    final List<String> mail = user.getMails();
                    if (mail.isEmpty()) {
                        if (RC.getSettings().isNotifyNoNewMail()) {
                            user.sendMessage(tl("noNewMail")); // Only notify if they want us to.
                        }
                    } else {
                        user.sendMessage(tl("youHaveNewMail", mail.size()));
                    }
                }

                if (user.isAuthorized("refinecraft.fly.safelogin")) {
                    user.getBase().setFallDistance(0);
                    if (LocationUtil.shouldFly(user.getLocation())) {
                        user.getBase().setAllowFlight(true);
                        user.getBase().setFlying(true);
                        if (RC.getSettings().isSendFlyEnableOnJoin()) {
                            user.getBase().sendMessage(tl("flyMode", tl("enabled"), user.getDisplayName()));
                        }
                    }
                }

                if (!user.isAuthorized("refinecraft.speed")) {
                    user.getBase().setFlySpeed(0.1f);
                    user.getBase().setWalkSpeed(0.2f);
                }

                if (user.isSocialSpyEnabled() && !user.isAuthorized("refinecraft.socialspy")) {
                    user.setSocialSpyEnabled(false);
                    RC.getLogger().log(Level.INFO, "Set socialspy to false for {0} because they had it enabled without permission.", user.getName());
                }

                user.stopTransaction();
            }
        }

        RC.scheduleSyncDelayedTask(new DelayJoinTask());
    }

    // Makes the compass item ingame always point to the first refinecraft home.  #EasterEgg
    private void updateCompass(final User user) {
        Location loc = user.getHome(user.getLocation());
        if (loc == null) {
            loc = user.getBase().getBedSpawnLocation();
        }
        if (loc != null) {
            user.getBase().setCompassTarget(loc);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        switch (event.getResult()) {
            case KICK_FULL:
                final User kfuser = RC.getUser(event.getPlayer());
                if (kfuser.isAuthorized("refinecraft.joinfullserver")) {
                    event.allow();
                    return;
                }
                event.disallow(Result.KICK_FULL, tl("serverFull"));
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final boolean backListener = RC.getSettings().registerBackInListener();
        final boolean teleportInvulnerability = RC.getSettings().isTeleportInvulnerability();
        if (backListener || teleportInvulnerability) {
            final User user = RC.getUser(event.getPlayer());
            //There is TeleportCause.COMMMAND but plugins have to actively pass the cause in on their teleports.
            if (backListener && (event.getCause() == TeleportCause.PLUGIN || event.getCause() == TeleportCause.COMMAND)) {
                user.setLastLocation();
            }
            if (teleportInvulnerability && (event.getCause() == TeleportCause.PLUGIN || event.getCause() == TeleportCause.COMMAND)) {
                user.enableInvulnerabilityAfterTeleport();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerEggThrow(final PlayerEggThrowEvent event) {
        final User user = RC.getUser(event.getPlayer());
        final ItemStack stack = new ItemStack(Material.EGG, 1);
        if (user.hasUnlimited(stack)) {
            user.getBase().getInventory().addItem(stack);
            user.getBase().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        final User user = RC.getUser(event.getPlayer());
        if (user.hasUnlimited(new ItemStack(event.getBucket()))) {
            event.getItemStack().setType(event.getBucket());
            RC.scheduleSyncDelayedTask(new Runnable() {
                @Override
                public void run() {
                    user.getBase().updateInventory();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String cmd = event.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].replace("/", "").toLowerCase(Locale.ENGLISH);
        if (RC.getUser(player).isMuted() && (RC.getSettings().getMuteCommands().contains(cmd) || RC.getSettings().getMuteCommands().contains("*"))) {
            event.setCancelled(true);
            player.sendMessage(tl("voiceSilenced"));
            LOGGER.info(tl("mutedUserSpeaks", player.getName()));
            return;
        }
        if (RC.getSettings().getSocialSpyCommands().contains(cmd) || RC.getSettings().getSocialSpyCommands().contains("*")) {
            if (!player.hasPermission("refinecraft.chat.spy.exempt")) {
                for (User spyer : RC.getOnlineUsers()) {
                    if (spyer.isSocialSpyEnabled() && !player.equals(spyer.getBase())) {
                        spyer.sendMessage(player.getDisplayName() + " : " + event.getMessage());
                    }
                }
            }
        }
        
        boolean broadcast = true; // whether to broadcast the updated activity
        boolean update = true; // Only modified when the command is afk

        PluginCommand pluginCommand = RC.getServer().getPluginCommand(cmd);
        if (pluginCommand != null) {
            // Switch case for commands that shouldn't broadcast afk activity.
            switch (pluginCommand.getName()) {
                case "afk":
                    update = false;
                case "ghost":
                    broadcast = false;
            }
        }
        if (update) {
            final User user = RC.getUser(player);
            user.updateActivity(broadcast);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChangedWorldFlyReset(final PlayerChangedWorldEvent event) {
        final User user = RC.getUser(event.getPlayer());
        if (user.getBase().getGameMode() != GameMode.CREATIVE && !user.isAuthorized("refinecraft.fly")) {
            user.getBase().setFallDistance(0f);
            user.getBase().setAllowFlight(false);
        }
        if (!user.isAuthorized("refinecraft.speed")) {
            user.getBase().setFlySpeed(0.1f);
            user.getBase().setWalkSpeed(0.2f);
        } else {
            if (user.getBase().getFlySpeed() > RC.getSettings().getMaxFlySpeed() && !user.isAuthorized("refinecraft.speed.bypass")) {
                user.getBase().setFlySpeed((float) RC.getSettings().getMaxFlySpeed());
            } else {
                user.getBase().setFlySpeed(user.getBase().getFlySpeed() * 0.99999f);
            }

            if (user.getBase().getWalkSpeed() > RC.getSettings().getMaxWalkSpeed() && !user.isAuthorized("refinecraft.speed.bypass")) {
                user.getBase().setWalkSpeed((float) RC.getSettings().getMaxWalkSpeed());
            } else {
                user.getBase().setWalkSpeed(user.getBase().getWalkSpeed() * 0.99999f);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final User user = RC.getUser(event.getPlayer());
        final String newWorld = event.getPlayer().getLocation().getWorld().getName();
        user.setDisplayNick();
        updateCompass(user);
        if (RC.getSettings().getNoGodWorlds().contains(newWorld) && user.isGodModeEnabledRaw()) {
            user.sendMessage(tl("noGodWorldWarning"));
        }

        if (!user.getWorld().getName().equals(newWorld)) {
            user.sendMessage(tl("currentWorld", newWorld));
        }
        if (user.isGhost()) {
            user.setGhosted(user.isAuthorized("refinecraft.ghost"));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (!event.isCancelled() && event.getClickedBlock().getType() == Material.BED_BLOCK && RC.getSettings().getUpdateBedAtDaytime()) {
                    User player = RC.getUser(event.getPlayer());
                    if (player.isAuthorized("refinecraft.sethome.bed")) {
                        player.getBase().setBedSpawnLocation(event.getClickedBlock().getLocation());
                        player.sendMessage(tl("bedSet", player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                    }
                }
                break;
            case LEFT_CLICK_AIR:
                if (event.getPlayer().isFlying()) {
                    final User user = RC.getUser(event.getPlayer());
                    if (user.isFlyClickJump()) {
                        useFlyClickJump(user);
                        return;
                    }
                }
            case LEFT_CLICK_BLOCK:
                if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
                    final User user = RC.getUser(event.getPlayer());
                    user.updateActivity(true);
                    if (user.hasPowerTools() && user.arePowerToolsEnabled() && usePowertools(user, event.getItem().getTypeId())) {
                        event.setCancelled(true);
                    }
                }
                break;
            default:
                break;
        }
    }

    // This method allows the /jump lock feature to work, allows teleporting while flying #EasterEgg
    private void useFlyClickJump(final User user) {
        try {
            final Location otarget = LocationUtil.getTarget(user.getBase());

            class DelayedClickJumpTask implements Runnable {
                @Override
                public void run() {
                    Location loc = user.getLocation();
                    loc.setX(otarget.getX());
                    loc.setZ(otarget.getZ());
                    while (LocationUtil.isBlockDamaging(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ())) {
                        loc.setY(loc.getY() + 1d);
                    }
                    user.getBase().teleport(loc, TeleportCause.PLUGIN);
                }
            }
            RC.scheduleSyncDelayedTask(new DelayedClickJumpTask());
        } catch (Exception ex) {
            if (RC.getSettings().isDebug()) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    private boolean usePowertools(final User user, final int id) {
        final List<String> commandList = user.getPowertool(id);
        if (commandList == null || commandList.isEmpty()) {
            return false;
        }
        boolean used = false;
        // We need to loop through each command and execute
        for (final String command : commandList) {
            if (command.contains("{player}")) {
                continue;
            } else if (command.startsWith("c:")) {
                used = true;
                user.getBase().chat(command.substring(2));
            } else {
                used = true;

                class PowerToolUseTask implements Runnable {
                    @Override
                    public void run() {
                        user.getServer().dispatchCommand(user.getBase(), command);
                        LOGGER.log(Level.INFO, String.format("[PT] %s issued server command: /%s", user.getName(), command));
                    }
                }
                RC.scheduleSyncDelayedTask(new PowerToolUseTask());

            }
        }
        return used;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (RC.getSettings().getDisableItemPickupWhileAfk()) {
            if (RC.getUser(event.getPlayer()).isAfk()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClickEvent(final InventoryClickEvent event) {
        Player refreshPlayer = null;
        final Inventory top = event.getView().getTopInventory();
        final InventoryType type = top.getType();

        if (type == InventoryType.PLAYER) {
            final User user = RC.getUser((Player) event.getWhoClicked());
            final InventoryHolder invHolder = top.getHolder();
            if (invHolder != null && invHolder instanceof HumanEntity) {
                final User invOwner = RC.getUser((Player) invHolder);
                if (user.isInvSee() && (!user.isAuthorized("refinecraft.invsee.modify") || invOwner.isAuthorized("refinecraft.invsee.preventmodify") || !invOwner.getBase().isOnline())) {
                    event.setCancelled(true);
                    refreshPlayer = user.getBase();
                }
            }
        } else if (type == InventoryType.ENDER_CHEST) {
            final User user = RC.getUser((Player) event.getWhoClicked());
            if (user.isEnderSee() && (!user.isAuthorized("refinecraft.enderchest.modify"))) {
                event.setCancelled(true);
                refreshPlayer = user.getBase();
            }
        } else if (type == InventoryType.WORKBENCH) {
            User user = RC.getUser((Player) event.getWhoClicked());
            if (user.isRecipeSee()) {
                event.setCancelled(true);
                refreshPlayer = user.getBase();
            }
        } else if (type == InventoryType.CHEST && top.getSize() == 9) {
            final User user = RC.getUser((Player) event.getWhoClicked());
            final InventoryHolder invHolder = top.getHolder();
            if (invHolder != null && invHolder instanceof HumanEntity && user.isInvSee()) {
                event.setCancelled(true);
                refreshPlayer = user.getBase();
            }
        }

        if (refreshPlayer != null) {
            final Player player = refreshPlayer;
            RC.scheduleSyncDelayedTask(new Runnable() {
                @Override
                public void run() {
                    player.updateInventory();
                }
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCloseEvent(final InventoryCloseEvent event) {
        Player refreshPlayer = null;
        final Inventory top = event.getView().getTopInventory();
        final InventoryType type = top.getType();
        if (type == InventoryType.PLAYER) {
            final User user = RC.getUser((Player) event.getPlayer());
            user.setInvSee(false);
            refreshPlayer = user.getBase();
        } else if (type == InventoryType.ENDER_CHEST) {
            final User user = RC.getUser((Player) event.getPlayer());
            user.setEnderSee(false);
            refreshPlayer = user.getBase();
        } else if (type == InventoryType.WORKBENCH) {
            final User user = RC.getUser((Player) event.getPlayer());
            if (user.isRecipeSee()) {
                user.setRecipeSee(false);
                event.getView().getTopInventory().clear();
                refreshPlayer = user.getBase();
            }
        } else if (type == InventoryType.CHEST && top.getSize() == 9) {
            final InventoryHolder invHolder = top.getHolder();
            if (invHolder != null && invHolder instanceof HumanEntity) {
                final User user = RC.getUser((Player) event.getPlayer());
                user.setInvSee(false);
                refreshPlayer = user.getBase();
            }
        }

        if (refreshPlayer != null) {
            final Player player = refreshPlayer;
            RC.scheduleSyncDelayedTask(new Runnable() {
                @Override
                public void run() {
                    player.updateInventory();
                }
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerFishEvent(final PlayerFishEvent event) {
        final User user = RC.getUser(event.getPlayer());
        user.updateActivity(true);
    }
}
