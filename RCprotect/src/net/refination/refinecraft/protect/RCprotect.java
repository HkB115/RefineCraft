package net.refination.refinecraft.protect;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RCprotect extends JavaPlugin implements InterfaceProtect {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final Map<ProtectConfig, Boolean> settingsBoolean = new EnumMap<ProtectConfig, Boolean>(ProtectConfig.class);
    private final Map<ProtectConfig, String> settingsString = new EnumMap<ProtectConfig, String>(ProtectConfig.class);
    private final Map<ProtectConfig, List<Integer>> settingsList = new EnumMap<ProtectConfig, List<Integer>>(ProtectConfig.class);
    private RefineCraftConnect ess = null;

    @Override
    public void onEnable() {
        final PluginManager pm = this.getServer().getPluginManager();
        final Plugin essPlugin = pm.getPlugin("RefineCraft");
        if (essPlugin == null || !essPlugin.isEnabled()) {
            enableEmergencyMode(pm);
            return;
        }
        ess = new RefineCraftConnect(essPlugin, this);

        final RCprotectBlockListener blockListener = new RCprotectBlockListener(this);
        pm.registerEvents(blockListener, this);

        final RCprotectEntityListener entityListener = new RCprotectEntityListener(this);
        pm.registerEvents(entityListener, this);

        final RCprotectWeatherListener weatherListener = new RCprotectWeatherListener(this);
        pm.registerEvents(weatherListener, this);
    }

    private void enableEmergencyMode(final PluginManager pm) {
        final EmergencyListener emListener = new EmergencyListener();
        pm.registerEvents(emListener, this);

        for (Player player : getServer().getOnlinePlayers()) {
            player.sendMessage("RefineCraft Protect is in emergency mode. Check your log for errors.");
        }
        LOGGER.log(Level.SEVERE, "RefineCraft not installed or failed to load. Essenials Protect is in emergency mode now.");
    }

    @Override
    public RefineCraftConnect getRefineCraftConnect() {
        return ess;
    }

    @Override
    public Map<ProtectConfig, Boolean> getSettingsBoolean() {
        return settingsBoolean;
    }

    @Override
    public Map<ProtectConfig, String> getSettingsString() {
        return settingsString;
    }

    @Override
    public Map<ProtectConfig, List<Integer>> getSettingsList() {
        return settingsList;
    }

    @Override
    public boolean getSettingBool(final ProtectConfig protectConfig) {
        final Boolean bool = settingsBoolean.get(protectConfig);
        return bool == null ? protectConfig.getDefaultValueBoolean() : bool;
    }

    @Override
    public String getSettingString(final ProtectConfig protectConfig) {
        final String str = settingsString.get(protectConfig);
        return str == null ? protectConfig.getDefaultValueString() : str;
    }
}
