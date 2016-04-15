package net.refination.api.events;

import net.refination.refinecraft.signs.RefineCraftSign;
import net.refination.api.InterfaceUser;

public class SignCreateEvent extends SignEvent {
    public SignCreateEvent(RefineCraftSign.InterfaceSign sign, RefineCraftSign RCSign, InterfaceUser user) {
        super(sign, RCSign, user);
    }
}
