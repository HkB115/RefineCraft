package net.refination.api.events;

import net.refination.api.InterfaceUser;
import org.bukkit.event.Cancellable;

/**
 * This handles common boilerplate for other StatusChangeEvents
 */
public class StatusChangeEvent extends StateChangeEvent implements Cancellable {
    private boolean newValue;

    public StatusChangeEvent(InterfaceUser affected, InterfaceUser controller, boolean value) {
        super(affected, controller);
        this.newValue = value;
    }

    public StatusChangeEvent(boolean isAsync, InterfaceUser affected, InterfaceUser controller, boolean value) {
        super(isAsync, affected, controller);
        this.newValue = value;
    }

    public boolean getValue() {
        return newValue;
    }
}
