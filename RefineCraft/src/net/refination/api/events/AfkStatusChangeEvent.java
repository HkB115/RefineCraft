package net.refination.api.events;

import net.refination.api.InterfaceUser;

public class AfkStatusChangeEvent extends StatusChangeEvent {
    public AfkStatusChangeEvent(InterfaceUser affected, boolean value) {
        super(affected, affected, value);
    }
}
