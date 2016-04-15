package net.refination.api.events;

import net.refination.api.InterfaceUser;

public class IgnoreStatusChangeEvent extends StatusChangeEvent {
    public IgnoreStatusChangeEvent(InterfaceUser affected, InterfaceUser controller, boolean value) {
        super(affected, controller, value);
    }
}
