package net.refination.refinecraft;

import net.refination.refinecraft.commands.InterfaceRefineCraftCommand;
import net.refination.refinecraft.signs.RefineCraftSign;
import net.refination.refinecraft.textreader.InterfaceText;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface InterfaceSettings extends InterfaceConf {
    boolean areSignsDisabled();

    InterfaceText getAnnounceNewPlayerFormat();

    boolean getAnnounceNewPlayers();

    String getNewPlayerKit();

    String getBackupCommand();

    long getBackupInterval();

    String getChatFormat(String group);

    int getChatRadius();

    int getNearRadius();

    char getChatShout();

    char getChatQuestion();

    BigDecimal getCommandCost(InterfaceRefineCraftCommand cmd);

    BigDecimal getCommandCost(String label);

    String getCurrencySymbol();

    int getOversizedStackSize();

    int getDefaultStackSize();

    double getHealCooldown();

    Set<String> getSocialSpyCommands();

    Set<String> getMuteCommands();

    Map<String, Object> getKit(String name);

    ConfigurationSection getKits();

    void addKit(String name, List<String> lines, long delay);

    boolean isSkippingUsedOneTimeKitsFromKitList();

    String getLocale();

    String getNewbieSpawn();

    String getNicknamePrefix();

    ChatColor getOperatorColor() throws Exception;

    boolean getPerWarpPermission();

    boolean getProtectBoolean(final String configName, boolean def);

    int getProtectCreeperMaxHeight();

    List<Integer> getProtectList(final String configName);

    boolean getProtectPreventSpawn(final String creatureName);

    String getProtectString(final String configName);

    boolean getRespawnAtHome();

    Set getMultipleHomes();

    int getHomeLimit(String set);

    int getHomeLimit(User user);

    int getSpawnMobLimit();

    BigDecimal getStartingBalance();

    boolean isTeleportSafetyEnabled();

    boolean isForceDisableTeleportSafety();

    double getTeleportCooldown();

    double getTeleportDelay();

    boolean hidePermissionlessHelp();

    boolean isCommandDisabled(final InterfaceRefineCraftCommand cmd);

    boolean isCommandDisabled(String label);

    boolean isCommandOverridden(String name);

    boolean isDebug();

    boolean isEcoDisabled();

    boolean isTradeInStacks(int id);

    List<Integer> itemSpawnBlacklist();

    List<RefineCraftSign> enabledSigns();

    boolean permissionBasedItemSpawn();

    boolean showNonEssCommandsInHelp();

    boolean warnOnBuildDisallow();

    boolean warnOnSmite();

    BigDecimal getMaxMoney();

    BigDecimal getMinMoney();

    boolean isEcoLogEnabled();

    boolean isEcoLogUpdateEnabled();

    boolean removeGodOnDisconnect();

    boolean changeDisplayName();

    boolean changePlayerListName();

    boolean isPlayerCommand(String string);

    boolean useBukkitPermissions();

    boolean addPrefixSuffix();

    boolean disablePrefix();

    boolean disableSuffix();

    long getAutoAfk();

    long getAutoAfkKick();

    boolean getFreezeAfkPlayers();

    boolean cancelAfkOnMove();

    boolean cancelAfkOnInteract();

    boolean isAfkListName();

    String getAfkListName();

    boolean areDeathMessagesEnabled();

    void setDebug(boolean debug);

    Set<String> getNoGodWorlds();

    boolean getUpdateBedAtDaytime();

    boolean allowUnsafeEnchantments();

    boolean getRepairEnchanted();

    boolean isWorldTeleportPermissions();

    boolean isWorldHomePermissions();

    boolean registerBackInListener();

    boolean getDisableItemPickupWhileAfk();

    EventPriority getRespawnPriority();

    long getTpaAcceptCancellation();

    long getTeleportInvulnerability();

    boolean isTeleportInvulnerability();

    long getLoginAttackDelay();

    int getSignUsePerSecond();

    double getMaxFlySpeed();

    double getMaxWalkSpeed();

    int getMailsPerMinute();

    long getEconomyLagWarning();

    long getPermissionsLagWarning();

    void setRCchatActive(boolean b);

    long getMaxTempban();

    Map<String, Object> getListGroupConfig();

    int getMaxNickLength();

    boolean ignoreColorsInMaxLength();

    int getMaxUserCacheCount();

    boolean allowSilentJoinQuit();

    boolean isCustomJoinMessage();

    String getCustomJoinMessage();

    boolean isCustomQuitMessage();

    String getCustomQuitMessage();

    boolean isNotifyNoNewMail();

    boolean isDropItemsIfFull();
    
    boolean isLastMessageReplyRecipient();
    
    BigDecimal getMinimumPayAmount();
    
    long getLastMessageReplyRecipientTimeout();

    boolean isMilkBucketEasterEggEnabled();

    boolean isSendFlyEnableOnJoin();
    
    boolean isWorldTimePermissions();
}
