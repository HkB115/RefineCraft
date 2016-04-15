package net.refination.refinecraft.signs;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;


public class SignEntityListener implements Listener {
    private final transient InterfaceRefineCraft RC;

    public SignEntityListener(final InterfaceRefineCraft RC) {
        this.RC = RC;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSignEntityExplode(final EntityExplodeEvent event) {
        if (RC.getSettings().areSignsDisabled()) {
            event.getHandlers().unregister(this);
            return;
        }

        for (Block block : event.blockList()) {
            if (((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) && RefineCraftSign.isValidSign(new RefineCraftSign.BlockSign(block))) || RefineCraftSign.checkIfBlockBreaksSigns(block)) {
                event.setCancelled(true);
                return;
            }
            for (RefineCraftSign sign : RC.getSettings().enabledSigns()) {
                if (sign.areHeavyEventRequired() && sign.getBlocks().contains(block.getType())) {
                    event.setCancelled(!sign.onBlockExplode(block, RC));
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSignEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (RC.getSettings().areSignsDisabled()) {
            event.getHandlers().unregister(this);
            return;
        }

        final Block block = event.getBlock();
        if (((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) && RefineCraftSign.isValidSign(new RefineCraftSign.BlockSign(block))) || RefineCraftSign.checkIfBlockBreaksSigns(block)) {
            event.setCancelled(true);
            return;
        }
        for (RefineCraftSign sign : RC.getSettings().enabledSigns()) {
            if (sign.areHeavyEventRequired() && sign.getBlocks().contains(block.getType()) && !sign.onBlockBreak(block, RC)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
