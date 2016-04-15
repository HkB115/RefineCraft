package net.refination.refinecraft.xmpp;

import net.refination.refinecraft.InterfaceRefineCraft;
import net.refination.api.InterfaceUser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;


public interface InterfaceRefineCraftXMPP extends Plugin {
    String getAddress(final Player user);

    String getAddress(final String name);

    List<String> getSpyUsers();

    InterfaceUser getUserByAddress(final String address);

    boolean sendMessage(final Player user, final String message);

    boolean sendMessage(final String address, final String message);

    void setAddress(final Player user, final String address);

    boolean toggleSpy(final Player user);

    void broadcastMessage(final InterfaceUser sender, final String message, final String xmppAddress);

    InterfaceRefineCraft getRC();
}
