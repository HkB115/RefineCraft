package net.refination.refinecraft;

import net.refination.refinecraft.register.payment.Methods;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.logging.Level;


public class RefineCraftPluginListener implements Listener, InterfaceConf {
    private final transient InterfaceRefineCraft RC;

    public RefineCraftPluginListener(final InterfaceRefineCraft RC) {
        this.RC = RC;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("RCchat")) {
            RC.getSettings().setRCchatActive(true);
        }
        RC.getPermissionsHandler().setUseSuperperms(RC.getSettings().useBukkitPermissions());
        RC.getPermissionsHandler().checkPermissions();
        RC.getAlternativeCommandsHandler().addPlugin(event.getPlugin());
        if (!Methods.hasMethod() && Methods.setMethod(RC.getServer().getPluginManager())) {
            RC.getLogger().log(Level.INFO, "Payment method found (" + Methods.getMethod().getLongName() + " version: " + RC.getPaymentMethod().getMethod().getVersion() + ")");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("RCchat")) {
            RC.getSettings().setRCchatActive(false);
        }
        RC.getAlternativeCommandsHandler().removePlugin(event.getPlugin());
        // Check to see if the plugin thats being disabled is the one we are using
        if (RC.getPaymentMethod() != null && Methods.hasMethod() && Methods.checkDisabled(event.getPlugin())) {
            Methods.reset();
            RC.getLogger().log(Level.INFO, "Payment method was disabled. No longer accepting payments.");
        }
    }

    @Override
    public void reloadConfig() {
    }
}
