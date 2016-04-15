package net.refination.api.events;

import net.refination.api.InterfaceUser;

public class JailStatusChangeEvent extends StatusChangeEvent {
    public JailStatusChangeEvent(InterfaceUser affected, InterfaceUser controller, boolean value) {
        super(affected, controller, value);
    }
}