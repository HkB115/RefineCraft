package net.refination.refinecraft.perm;

import org.bukkit.entity.Player;

import java.util.List;


public interface InterfacePermissionsHandler {
    String getGroup(Player base);

    List<String> getGroups(Player base);

    boolean canBuild(Player base, String group);

    boolean inGroup(Player base, String group);

    boolean hasPermission(Player base, String node);

    String getPrefix(Player base);

    String getSuffix(Player base);

    boolean tryProvider();
}
