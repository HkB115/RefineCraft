package net.refination.refinecraft;

import net.refination.refinecraft.utils.LocationUtil;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;


public class RefineCraftBlockListener implements Listener {
    private final transient InterfaceRefineCraft RC;

    public RefineCraftBlockListener(final InterfaceRefineCraft RC) {
        this.RC = RC;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        // Do not rely on getItemInHand();
        // http://leaky.bukkit.org/issues/663
        final ItemStack is = LocationUtil.convertBlockToItem(event.getBlockPlaced());
        if (is == null) {
            return;
        }

        if (is.getType() == Material.MOB_SPAWNER && event.getItemInHand() != null && event.getPlayer() != null && event.getItemInHand().getType() == Material.MOB_SPAWNER) {
            final BlockState blockState = event.getBlockPlaced().getState();
            if (blockState instanceof CreatureSpawner) {
                final CreatureSpawner spawner = (CreatureSpawner) blockState;
                final EntityType type = RC.getSpawnerProvider().getEntityType(event.getItemInHand());
                if (type != null && Mob.fromBukkitType(type) != null) {
                    if (RC.getUser(event.getPlayer()).isAuthorized("refinecraft.spawnerconvert." + Mob.fromBukkitType(type).name().toLowerCase(Locale.ENGLISH))) {
                        spawner.setSpawnedType(type);
                    }
                }
            }
        }

        final User user = RC.getUser(event.getPlayer());
        if (user.hasUnlimited(is) && user.getBase().getGameMode() == GameMode.SURVIVAL) {
            class UnlimitedItemSpawnTask implements Runnable {
                @Override
                public void run() {
                    user.getBase().getInventory().addItem(is);
                    user.getBase().updateInventory();
                }
            }
            RC.scheduleSyncDelayedTask(new UnlimitedItemSpawnTask());
        }
    }
}
