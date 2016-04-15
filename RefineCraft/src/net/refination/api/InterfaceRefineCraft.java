package net.refination.api;

import net.refination.nms.PotionMetaProvider;
import net.refination.nms.SpawnEggProvider;

public interface InterfaceRefineCraft extends net.refination.refinecraft.InterfaceRefineCraft {

    SpawnEggProvider getSpawnEggProvider();

    PotionMetaProvider getPotionMetaProvider();
}
