package net.refination.refinecraft.signs;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.logging.Level;


public class SignPlayerListener implements Listener {
    private final transient InterfaceRefineCraft RC;

    public SignPlayerListener(final InterfaceRefineCraft RC) {
        this.RC = RC;
    }

    //This following code below listens to cancelled events to fix a bukkit issue
    //Right clicking signs with a block in hand, can now fire cancelled events.
    //This is because when the block place is cancelled (for example not enough space for the block to be placed),
    //the event will be marked as cancelled, thus preventing 30% of sign purchases.
    @EventHandler(priority = EventPriority.LOW)
    public void onSignPlayerInteract(final PlayerInteractEvent event) {
        if (RC.getSettings().areSignsDisabled()) {
            event.getHandlers().unregister(this);
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        final Block block;
        if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_AIR) {
            Block targetBlock = null;
            try {
                targetBlock = event.getPlayer().getTargetBlock((HashSet<Byte>) null, 5);
            } catch (IllegalStateException ex) {
                if (RC.getSettings().isDebug()) {
                    RC.getLogger().log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            block = targetBlock;
        } else {
            block = event.getClickedBlock();
        }
        if (block == null) {
            return;
        }

        final Material mat = block.getType();
        if (mat == Material.SIGN_POST || mat == Material.WALL_SIGN) {
            final String csign = ((Sign) block.getState()).getLine(0);
            for (RefineCraftSign sign : RC.getSettings().enabledSigns()) {
                if (csign.equalsIgnoreCase(sign.getSuccessName())) {
                    sign.onSignInteract(block, event.getPlayer(), RC);
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            for (RefineCraftSign sign : RC.getSettings().enabledSigns()) {
                if (sign.areHeavyEventRequired() && sign.getBlocks().contains(block.getType()) && !sign.onBlockInteract(block, event.getPlayer(), RC)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
