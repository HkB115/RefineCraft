package net.refination.refinecraft.geoip;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class RefineCraftGeoIP extends JavaPlugin {
    public RefineCraftGeoIP() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        final PluginManager pm = getServer().getPluginManager();
        final InterfaceRefineCraft ess = (InterfaceRefineCraft) pm.getPlugin("RefineCraft");
        if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion())) {
            getLogger().log(Level.WARNING, tl("versionMismatchAll"));
        }
        if (!ess.isEnabled()) {
            this.setEnabled(false);
            return;
        }
        final RefineCraftGeoIPPlayerListener playerListener = new RefineCraftGeoIPPlayerListener(getDataFolder(), ess);
        pm.registerEvents(playerListener, this);


        getLogger().log(Level.INFO, "This product includes GeoLite data created by MaxMind, available from http://www.maxmind.com/.");
    }
}
