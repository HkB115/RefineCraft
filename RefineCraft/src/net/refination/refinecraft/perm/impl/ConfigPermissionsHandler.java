package net.refination.refinecraft.perm.impl;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ConfigPermissionsHandler extends SuperpermsHandler {
    private final transient InterfaceRefineCraft RC;

    public ConfigPermissionsHandler(final Plugin RC) {
        this.RC = (InterfaceRefineCraft) RC;
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        return true;
    }

    @Override
    public boolean hasPermission(final Player base, final String node) {
        final String[] cmds = node.split("\\.", 2);
        return RC.getSettings().isPlayerCommand(cmds[cmds.length - 1]) || super.hasPermission(base, node);
    }

    @Override
    public boolean tryProvider() {
        return true;
    }
}
