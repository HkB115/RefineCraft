package net.refination.refinecraft.protect;

import net.refination.refinecraft.InterfaceConf;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class RefineCraftConnect {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final InterfaceRefineCraft ess;
    private final InterfaceProtect protect;

    public RefineCraftConnect(Plugin essPlugin, Plugin essProtect) {
        if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion())) {
            LOGGER.log(Level.WARNING, tl("versionMismatchAll"));
        }
        ess = (InterfaceRefineCraft) essPlugin;
        protect = (InterfaceProtect) essProtect;
        ProtectReloader pr = new ProtectReloader();
        pr.reloadConfig();
        ess.addReloadListener(pr);
    }

    public InterfaceRefineCraft getRefineCraft() {
        return ess;
    }

    private class ProtectReloader implements InterfaceConf {
        @Override
        public void reloadConfig() {
            for (ProtectConfig protectConfig : ProtectConfig.values()) {
                if (protectConfig.isList()) {
                    protect.getSettingsList().put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
                } else if (protectConfig.isString()) {
                    protect.getSettingsString().put(protectConfig, ess.getSettings().getProtectString(protectConfig.getConfigName()));
                } else {
                    protect.getSettingsBoolean().put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
                }
            }
        }
    }
}