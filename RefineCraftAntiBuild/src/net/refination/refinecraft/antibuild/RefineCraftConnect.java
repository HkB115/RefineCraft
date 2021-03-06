package net.refination.refinecraft.antibuild;

import net.refination.refinecraft.InterfaceConf;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class RefineCraftConnect {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private final transient InterfaceRefineCraft ess;
    private final transient InterfaceAntiBuild protect;

    public RefineCraftConnect(Plugin essPlugin, Plugin essProtect) {
        if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion())) {
            LOGGER.log(Level.WARNING, tl("versionMismatchAll"));
        }
        ess = (InterfaceRefineCraft) essPlugin;
        protect = (InterfaceAntiBuild) essProtect;
        AntiBuildReloader pr = new AntiBuildReloader();
        pr.reloadConfig();
        ess.addReloadListener(pr);
    }

    public void onDisable() {
    }

    public InterfaceRefineCraft getRefineCraft() {
        return ess;
    }

    public void alert(final User user, final String item, final String type) {
        final Location loc = user.getLocation();
        final String warnMessage = tl("alertFormat", user.getName(), type, item, loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        LOGGER.log(Level.WARNING, warnMessage);
        for (Player p : ess.getServer().getOnlinePlayers()) {
            final User alertUser = ess.getUser(p);
            if (alertUser.isAuthorized("refinecraft.protect.alerts")) {
                alertUser.sendMessage(warnMessage);
            }
        }
    }


    private class AntiBuildReloader implements InterfaceConf {
        @Override
        public void reloadConfig() {
            for (AntiBuildConfig protectConfig : AntiBuildConfig.values()) {
                if (protectConfig.isList()) {
                    protect.getSettingsList().put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
                } else {
                    protect.getSettingsBoolean().put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
                }

            }

        }
    }
}
