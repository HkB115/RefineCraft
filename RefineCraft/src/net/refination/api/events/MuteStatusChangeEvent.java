package net.refination.api.events;

import net.refination.api.InterfaceUser;

public class MuteStatusChangeEvent extends StatusChangeEvent {
    public MuteStatusChangeEvent(InterfaceUser affected, InterfaceUser controller, boolean value) {
        super(affected, controller, value);
    }
}
