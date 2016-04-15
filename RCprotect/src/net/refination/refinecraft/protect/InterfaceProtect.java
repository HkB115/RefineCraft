package net.refination.refinecraft.protect;

import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;


public interface InterfaceProtect extends Plugin {
    boolean getSettingBool(final ProtectConfig protectConfig);

    String getSettingString(final ProtectConfig protectConfig);

    RefineCraftConnect getRefineCraftConnect();

    Map<ProtectConfig, Boolean> getSettingsBoolean();

    Map<ProtectConfig, String> getSettingsString();

    Map<ProtectConfig, List<Integer>> getSettingsList();
}
