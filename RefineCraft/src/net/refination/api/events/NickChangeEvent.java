package net.refination.api.events;

import net.refination.api.InterfaceUser;
import org.bukkit.event.Cancellable;

public class NickChangeEvent extends StateChangeEvent implements Cancellable {
    private String newValue;

    public NickChangeEvent(InterfaceUser affected, InterfaceUser controller, String value) {
        super(affected, controller);
        this.newValue = value;
    }

    public String getValue() {
        return newValue;
    }
}
