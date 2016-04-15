package net.refination.refinecraft.antibuild;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class RefineCraftAntiBuild extends JavaPlugin implements InterfaceAntiBuild {
    private final transient Map<AntiBuildConfig, Boolean> settingsBoolean = new EnumMap<AntiBuildConfig, Boolean>(AntiBuildConfig.class);
    private final transient Map<AntiBuildConfig, List<Integer>> settingsList = new EnumMap<AntiBuildConfig, List<Integer>>(AntiBuildConfig.class);
    private transient RefineCraftConnect ess = null;

    @Override
    public void onEnable() {
        final PluginManager pm = this.getServer().getPluginManager();
        final Plugin essPlugin = pm.getPlugin("RefineCraft");
        if (essPlugin == null || !essPlugin.isEnabled()) {
            return;
        }
        ess = new RefineCraftConnect(essPlugin, this);

        final RefineCraftAntiBuildListener blockListener = new RefineCraftAntiBuildListener(this);
        pm.registerEvents(blockListener, this);
    }

    @Override
    public boolean checkProtectionItems(final AntiBuildConfig list, final int id) {
        final List<Integer> itemList = settingsList.get(list);
        return itemList != null && !itemList.isEmpty() && itemList.contains(id);
    }

    @Override
    public RefineCraftConnect getRefineCraftConnect() {
        return ess;
    }

    @Override
    public Map<AntiBuildConfig, Boolean> getSettingsBoolean() {
        return settingsBoolean;
    }

    @Override
    public Map<AntiBuildConfig, List<Integer>> getSettingsList() {
        return settingsList;
    }

    @Override
    public boolean getSettingBool(final AntiBuildConfig protectConfig) {
        final Boolean bool = settingsBoolean.get(protectConfig);
        return bool == null ? protectConfig.getDefaultValueBoolean() : bool;
    }
}
