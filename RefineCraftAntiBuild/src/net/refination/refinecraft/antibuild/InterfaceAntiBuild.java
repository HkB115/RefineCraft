package net.refination.refinecraft.antibuild;

import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;


public interface InterfaceAntiBuild extends Plugin {
    boolean checkProtectionItems(final AntiBuildConfig list, final int id);

    boolean getSettingBool(final AntiBuildConfig protectConfig);

    RefineCraftConnect getRefineCraftConnect();

    Map<AntiBuildConfig, Boolean> getSettingsBoolean();

    Map<AntiBuildConfig, List<Integer>> getSettingsList();
}
