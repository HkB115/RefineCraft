package net.refination.refinecraft.xmpp;

import net.refination.refinecraft.InterfaceRefineCraft;
import net.refination.api.InterfaceUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class RefineCraftXMPP extends JavaPlugin implements InterfaceRefineCraftXMPP {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private static RefineCraftXMPP instance = null;
    private transient UserManager users;
    private transient XMPPManager xmpp;
    private transient InterfaceRefineCraft RC;

    public static InterfaceRefineCraftXMPP getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        final PluginManager pluginManager = getServer().getPluginManager();
        RC = (InterfaceRefineCraft) pluginManager.getPlugin("RefineCraft");
        if (!this.getDescription().getVersion().equals(RC.getDescription().getVersion())) {
            LOGGER.log(Level.WARNING, tl("versionMismatchAll"));
        }
        if (!RC.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        final RefineCraftXMPPPlayerListener playerListener = new RefineCraftXMPPPlayerListener(RC);
        pluginManager.registerEvents(playerListener, this);

        users = new UserManager(this.getDataFolder());
        xmpp = new XMPPManager(this);

        RC.addReloadListener(users);
        RC.addReloadListener(xmpp);
    }

    @Override
    public void onDisable() {
        if (xmpp != null) {
            xmpp.disconnect();
        }
        instance = null;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        return RC.onCommandRefineCraft(sender, command, commandLabel, args, RefineCraftXMPP.class.getClassLoader(), "net.refination.refinecraft.xmpp.Command", "refinecraft.", null);
    }

    @Override
    public void setAddress(final Player user, final String address) {
        final String username = user.getName().toLowerCase(Locale.ENGLISH);
        instance.users.setAddress(username, address);
    }

    @Override
    public String getAddress(final String name) {
        return instance.users.getAddress(name);
    }

    @Override
    public InterfaceUser getUserByAddress(final String address) {
        String username = instance.users.getUserByAddress(address);
        return username == null ? null : RC.getUser(username);
    }

    @Override
    public boolean toggleSpy(final Player user) {
        final String username = user.getName().toLowerCase(Locale.ENGLISH);
        final boolean spy = !instance.users.isSpy(username);
        instance.users.setSpy(username, spy);
        return spy;
    }

    @Override
    public String getAddress(final Player user) {
        return instance.users.getAddress(user.getName());
    }

    @Override
    public boolean sendMessage(final Player user, final String message) {
        return instance.xmpp.sendMessage(instance.users.getAddress(user.getName()), message);
    }

    @Override
    public boolean sendMessage(final String address, final String message) {
        return instance.xmpp.sendMessage(address, message);
    }

    // @Override
    public static boolean updatePresence() {
        instance.xmpp.updatePresence();
        return true;
    }

    @Override
    public List<String> getSpyUsers() {
        return instance.users.getSpyUsers();
    }

    @Override
    public void broadcastMessage(final InterfaceUser sender, final String message, final String xmppAddress) {
        RC.broadcastMessage(sender, message);
        try {
            for (String address : getSpyUsers()) {
                if (!address.equalsIgnoreCase(xmppAddress)) {
                    sendMessage(address, message);
                }
            }
        } catch (Exception ex) {
            // Ignore exceptions
        }
    }

    @Override
    public InterfaceRefineCraft getRC() {
        return RC;
    }
}
