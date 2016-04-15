package net.refination.refinecraft.settings;

import net.refination.refinecraft.storage.MapValueType;
import net.refination.refinecraft.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;


@Data @EqualsAndHashCode(callSuper = false) public class Jails implements StorageObject {
    @MapValueType(Location.class)
    private Map<String, Location> jails = new HashMap<String, Location>();
}
