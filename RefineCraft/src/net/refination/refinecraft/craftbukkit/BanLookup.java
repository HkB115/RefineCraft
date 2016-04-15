package net.refination.refinecraft.craftbukkit;

import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.util.Set;


public class BanLookup {
    public static Boolean isBanned(InterfaceRefineCraft RC, User user) {
        return isBanned(RC, user.getName());
    }

    public static Boolean isBanned(InterfaceRefineCraft RC, String name) {
        return getBanEntry(RC, name) != null;
    }

    public static BanEntry getBanEntry(InterfaceRefineCraft RC, String name) {
        Set<BanEntry> benteries = RC.getServer().getBanList(BanList.Type.NAME).getBanEntries();
        for (BanEntry banEnt : benteries) {
            if (banEnt.getTarget().equals(name)) {
                return banEnt;
            }
        }
        return null;
    }

}
