package net.refination.refinecraft.protect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class RCprotectWeatherListener implements Listener {
    private final InterfaceProtect prot;

    public RCprotectWeatherListener(final InterfaceProtect prot) {
        this.prot = prot;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent event) {
        if (prot.getSettingBool(ProtectConfig.disable_weather_storm) && event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLightningStrike(final LightningStrikeEvent event) {
        if (prot.getSettingBool(ProtectConfig.disable_weather_lightning)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onThunderChange(final ThunderChangeEvent event) {
        if (prot.getSettingBool(ProtectConfig.disable_weather_thunder) && event.toThunderState()) {
            event.setCancelled(true);
        }
    }
}
