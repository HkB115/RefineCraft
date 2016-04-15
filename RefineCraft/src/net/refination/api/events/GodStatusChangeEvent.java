package net.refination.api.events;

import net.refination.api.InterfaceUser;

public class GodStatusChangeEvent extends StatusChangeEvent {
    public GodStatusChangeEvent(InterfaceUser affected, InterfaceUser controller, boolean value) {
        super(affected, controller, value);
    }
}
