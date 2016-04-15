package net.refination.nms;

import net.refination.providers.Provider;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public abstract class SpawnEggProvider implements Provider {
    public abstract ItemStack createEggItem(EntityType type) throws IllegalArgumentException;
    public abstract EntityType getSpawnedType(ItemStack eggItem) throws IllegalArgumentException;

    @Override
    public boolean tryProvider() {
        try {
            EntityType type = EntityType.CREEPER;
            ItemStack is = createEggItem(type);
            EntityType readType = getSpawnedType(is);
            return type == readType;
        } catch (Throwable t) {
            return false;
        }
    }
}
